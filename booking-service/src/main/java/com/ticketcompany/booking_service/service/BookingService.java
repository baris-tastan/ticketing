package com.ticketcompany.booking_service.service;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;
import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.dto.BookingResponse;
import com.ticketcompany.booking_service.dto.HoldResponse;
import com.ticketcompany.booking_service.exception.BookingNotFoundException;
import com.ticketcompany.booking_service.exception.BookingNotPayableException;
import com.ticketcompany.booking_service.exception.SeatNotFoundException;
import com.ticketcompany.booking_service.exception.SeatUnavailableException;
import com.ticketcompany.booking_service.messaging.PaymentRequestPublisher;
import com.ticketcompany.booking_service.messaging.PaymentRequestedEvent;
import com.ticketcompany.booking_service.repository.BookingRepository;
import com.ticketcompany.booking_service.repository.SeatRepository;

@Service
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;
    private final PaymentRequestPublisher paymentRequestPublisher;

    public BookingService(SeatRepository seatRepository, BookingRepository bookingRepository,
                          SeatHoldService seatHoldService, PaymentRequestPublisher paymentRequestPublisher) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.seatHoldService = seatHoldService;
        this.paymentRequestPublisher = paymentRequestPublisher;
    }

    public void pay(Long bookingId, String payerEmail, String cardNumber) {
        Booking booking = bookingRepository.findWithSeatById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getCustomerEmail().equals(payerEmail)) {
            throw new BookingNotFoundException(bookingId);
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingNotPayableException(bookingId, "it is " + booking.getStatus());
        }
        if (booking.isExpired(Instant.now())) throw new BookingNotPayableException(bookingId, "its payment deadline has passed");

        paymentRequestPublisher.publish(new PaymentRequestedEvent(
                bookingId, booking.getSeat().getPrice(), toPaymentToken(cardNumber)));

        return;
    }

    private static String toPaymentToken(String cardNumber) {
        String lastFour = cardNumber.length() >= 4
                ? cardNumber.substring(cardNumber.length() - 4)
                : cardNumber;
        return "tok_" + lastFour;
    }
    


    public BookingResponse createBooking(Long seatId, String customerEmail) {
        HoldResponse hold = seatHoldService.holdSeat(seatId, customerEmail);      // ← now returns the deadline

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        bookingRepository.cancelExpiredPendingBooking(seatId, Instant.now());

        Booking booking;
        try {
            booking = bookingRepository.save(new Booking(seat, customerEmail, hold.expiresAt()));   // ← passes it on
        } catch (DataIntegrityViolationException e) {
            throw new SeatUnavailableException(seatId);
        }

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, String requesterEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getCustomerEmail().equals(requesterEmail)) {
            throw new BookingNotFoundException(bookingId);
        }

        return BookingResponse.from(booking);
    }
    @Transactional
    public void cancelIfStillPending(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
            return;
        }
        booking.cancel();
    }
}