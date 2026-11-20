package com.ticketcompany.booking_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketcompany.booking_service.dto.SeatResponse;
import com.ticketcompany.booking_service.service.SeatService;

@RestController
@RequestMapping("/events")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/{eventId}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long eventId) {
        return seatService.getSeatsForEvent(eventId);
    }
}