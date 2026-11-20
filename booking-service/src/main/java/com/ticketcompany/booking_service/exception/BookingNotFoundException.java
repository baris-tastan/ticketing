package com.ticketcompany.booking_service.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long bookingId) {
        super("Booking " + bookingId + " not found");
    }
}