package com.ticketcompany.booking_service.exception;

public class BookingNotPayableException extends RuntimeException {

    public BookingNotPayableException(Long bookingId, String reason) {
        super("Booking " + bookingId + " cannot be paid: " + reason);
    }
}