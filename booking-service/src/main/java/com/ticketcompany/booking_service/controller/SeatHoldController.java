package com.ticketcompany.booking_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketcompany.booking_service.dto.HoldResponse;
import com.ticketcompany.booking_service.dto.HoldSeatRequest;
import com.ticketcompany.booking_service.service.SeatHoldService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/seats")
public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    public SeatHoldController(SeatHoldService seatHoldService) {
        this.seatHoldService = seatHoldService;
    }

    @PostMapping("/{seatId}/hold")
    public HoldResponse holdSeat(@PathVariable Long seatId,
                                 @AuthenticationPrincipal Jwt jwt) {
        return seatHoldService.holdSeat(seatId, jwt.getSubject());
    }
}