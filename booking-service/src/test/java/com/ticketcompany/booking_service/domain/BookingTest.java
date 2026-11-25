package com.ticketcompany.booking_service.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class BookingTest {

    private static final Instant DEADLINE = Instant.parse("2026-01-01T12:10:00Z");

    @Test
    void newBookingIsPending() {
        Booking booking = newBooking();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void isNotExpiredBeforeDeadline() {
        Booking booking = newBooking();

        assertThat(booking.isExpired(DEADLINE.minusSeconds(1))).isFalse();
    }

    @Test
    void isExpiredExactlyAtDeadline() {
        Booking booking = newBooking();

        assertThat(booking.isExpired(DEADLINE)).isTrue();
    }

    @Test
    void isExpiredAfterDeadline() {
        Booking booking = newBooking();

        assertThat(booking.isExpired(DEADLINE.plusSeconds(1))).isTrue();
    }

    @Test
    void confirmMakesBookingConfirmed() {
        Booking booking = newBooking();

        booking.confirm();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void cannotConfirmCancelledBooking() {
        Booking booking = newBooking();
        booking.cancel();

        assertThatThrownBy(booking::confirm).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cannotCancelTwice() {
        Booking booking = newBooking();
        booking.cancel();

        assertThatThrownBy(booking::cancel).isInstanceOf(IllegalStateException.class);
    }

    private Booking newBooking() {
        return new Booking(null, "ali@example.com", DEADLINE);
    }
}