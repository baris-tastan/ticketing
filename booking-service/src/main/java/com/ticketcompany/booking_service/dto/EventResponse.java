package com.ticketcompany.booking_service.dto;

import java.time.Instant;

import com.ticketcompany.booking_service.domain.Event;

public record EventResponse(Long id, String name, String venue, Instant startsAt) {

    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getVenue(), event.getStartsAt());
    }
}