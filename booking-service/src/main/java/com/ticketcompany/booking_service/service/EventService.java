package com.ticketcompany.booking_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketcompany.booking_service.domain.Event;
import com.ticketcompany.booking_service.dto.EventResponse;
import com.ticketcompany.booking_service.exception.EventNotFoundException;
import com.ticketcompany.booking_service.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        List<EventResponse> result = new ArrayList<>();
        for (Event event : eventRepository.findAllByOrderByStartsAtAsc()) {
            result.add(EventResponse.from(event));
        }
        return result;
    }

    public EventResponse getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return EventResponse.from(event);
    }
}