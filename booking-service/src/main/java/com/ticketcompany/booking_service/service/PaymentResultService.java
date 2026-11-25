package com.ticketcompany.booking_service.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;
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
    public void applyPaymentResult(Long bookingId, PaymentOutcome outcome) {
        Optional<Booking> found = bookingRepository.findWithSeatById(bookingId);
        if (found.isEmpty()) {
            log.warn("Ignoring payment result for unknown booking {}", bookingId);
            return;
        }
        Booking booking = found.get();

        if (booking.getStatus() != BookingStatus.PENDING) {
            handleResultForFinishedBooking(booking, outcome);
            return;
        }

        if (outcome == PaymentOutcome.DECLINED) {
            booking.cancel();
            return;
        }

        if (booking.isExpired(Instant.now())) {
            booking.cancel();
            log.warn("REFUND NEEDED: booking {} was paid after its deadline", bookingId);
            return;
        }

        booking.confirm();
        booking.getSeat().markSold();
    }

    private void handleResultForFinishedBooking(Booking booking, PaymentOutcome outcome) {
        if (outcome == PaymentOutcome.DECLINED) {
            log.info("Ignoring DECLINED result for booking {}: it is already {}", booking.getId(), booking.getStatus());
            return;
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            log.info("Ignoring duplicate APPROVED result for booking {}", booking.getId());
            return;
        }
        log.warn("REFUND NEEDED: booking {} was paid but is already {}", booking.getId(), booking.getStatus());
    }
}