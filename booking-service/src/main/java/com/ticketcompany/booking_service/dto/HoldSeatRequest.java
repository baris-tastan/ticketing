package com.ticketcompany.booking_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record HoldSeatRequest(
        @NotBlank @Email String customerEmail
) {}