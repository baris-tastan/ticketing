package com.ticketcompany.booking_service.dto;

import java.time.Instant;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;

public record BookingResponse(
        Long id,
        Long seatId,
        String seatNumber,
        String customerEmail,
        BookingStatus status,
        Instant createdAt,
        Instant expiresAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSeat().getId(),
                booking.getSeat().getSeatNumber(),
                booking.getCustomerEmail(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getExpiresAt());
    }
}