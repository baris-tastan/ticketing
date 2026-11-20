package com.ticketcompany.booking_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketcompany.booking_service.dto.EventResponse;
import com.ticketcompany.booking_service.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }
}