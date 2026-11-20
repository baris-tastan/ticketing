package com.ticketcompany.booking_service.exception;

public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(Long seatId) {
        super("Seat " + seatId + " not found");
    }
}