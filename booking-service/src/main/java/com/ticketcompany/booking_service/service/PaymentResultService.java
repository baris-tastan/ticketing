package com.ticketcompany.booking_service.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;
import com.ticketcompany.booking_service.dto.BookingResponse;
import com.ticketcompany.booking_service.exception.BookingNotFoundException;
import com.ticketcompany.booking_service.exception.BookingNotPayableException;
import com.ticketcompany.booking_service.payment.PaymentOutcome;
import com.ticketcompany.booking_service.repository.BookingRepository;

@Service
public class PaymentResultService {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultService.class);

    private final BookingRepository bookingRepository;

    public PaymentResultService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingResponse applyPaymentResult(Long bookingId, PaymentOutcome outcome) {
        Booking booking = bookingRepository.findWithSeatById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        // 1. The booking changed while the bank was working (e.g. the timeout job cancelled it)
        if (booking.getStatus() != BookingStatus.PENDING) {
            if (outcome == PaymentOutcome.APPROVED) {
                log.warn("Booking {} is {} but its payment was approved: REFUND NEEDED",
                        bookingId, booking.getStatus());
            }
            throw new BookingNotPayableException(bookingId, "it is " + booking.getStatus());
        }

        // 2. The bank said no
        if (outcome == PaymentOutcome.DECLINED) {
            booking.cancel();
            return BookingResponse.from(booking);
        }

        // 3. Approved, but the deadline passed while the bank was working
        if (!Instant.now().isBefore(booking.getExpiresAt())) {
            booking.cancel();
            log.warn("Payment for booking {} arrived after its deadline: REFUND NEEDED", bookingId);
            return BookingResponse.from(booking);
        }

        // 4. Approved, in time: sell the seat
        booking.confirm();
        booking.getSeat().markSold();
        return BookingResponse.from(booking);
    }
}