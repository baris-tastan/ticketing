package com.ticketcompany.booking_service.dto;

import java.time.Instant;

public record HoldResponse(Long seatId, Instant expiresAt) {}