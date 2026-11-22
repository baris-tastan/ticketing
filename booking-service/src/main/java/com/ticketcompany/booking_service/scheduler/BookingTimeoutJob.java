package com.ticketcompany.booking_service.scheduler;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;
import com.ticketcompany.booking_service.repository.BookingRepository;
import com.ticketcompany.booking_service.service.BookingService;

@Component
public class BookingTimeoutJob {

    private static final Logger log = LoggerFactory.getLogger(BookingTimeoutJob.class);

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public BookingTimeoutJob(BookingRepository bookingRepository, BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @Scheduled(fixedDelay = 600000)
    public void cancelExpiredBookings() {
        List<Booking> expired =
                bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, Instant.now());

        for (Booking booking : expired) {
            try {
                bookingService.cancelIfStillPending(booking.getId());
                log.info("Cancelled expired booking {}", booking.getId());
            } catch (RuntimeException e) {
                log.warn("Could not cancel booking {}: {}", booking.getId(), e.getMessage());
            }
        }
    }
}