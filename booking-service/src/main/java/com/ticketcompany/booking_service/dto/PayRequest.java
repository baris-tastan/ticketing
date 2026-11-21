package com.ticketcompany.booking_service.dto;

import jakarta.validation.constraints.NotBlank;

public record PayRequest(
        @NotBlank String cardNumber
) {}