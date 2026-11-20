package com.ticketcompany.booking_service.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;
import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.dto.BookingResponse;
import com.ticketcompany.booking_service.dto.HoldResponse;
import com.ticketcompany.booking_service.exception.BookingNotFoundException;
import com.ticketcompany.booking_service.exception.SeatNotFoundException;
import com.ticketcompany.booking_service.exception.SeatUnavailableException;
import com.ticketcompany.booking_service.repository.BookingRepository;
import com.ticketcompany.booking_service.repository.SeatRepository;

@Service
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;

    public BookingService(SeatRepository seatRepository, BookingRepository bookingRepository,
                          SeatHoldService seatHoldService) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.seatHoldService = seatHoldService;
    }

    public BookingResponse createBooking(Long seatId, String customerEmail) {
        HoldResponse hold = seatHoldService.holdSeat(seatId, customerEmail);      // ← now returns the deadline

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

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