package com.ticketcompany.booking_service.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long eventId) {
        super("Event " + eventId + " not found");
    }
}