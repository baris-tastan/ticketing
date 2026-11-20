package com.ticketcompany.booking_service.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull Long seatId
) {}