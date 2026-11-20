package com.ticketcompany.booking_service.dto;

import java.math.BigDecimal;

import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.domain.SeatStatus;

public record SeatResponse(Long id, String seatNumber, BigDecimal price, SeatAvailability status) {

    public static SeatResponse from(Seat seat, boolean held) {
        SeatAvailability availability;
        if (seat.getStatus() == SeatStatus.SOLD) {
            availability = SeatAvailability.SOLD;
        } else if (held) {
            availability = SeatAvailability.HELD;
        } else {
            availability = SeatAvailability.AVAILABLE;
        }
        return new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getPrice(), availability);
    }
}