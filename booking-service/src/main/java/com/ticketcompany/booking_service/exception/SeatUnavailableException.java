package com.ticketcompany.booking_service.exception;

public class SeatUnavailableException extends RuntimeException {

    public SeatUnavailableException(Long seatId) {
        super("Seat " + seatId + " is not available");
    }
}