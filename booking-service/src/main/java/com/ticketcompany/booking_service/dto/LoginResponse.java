package com.ticketcompany.booking_service.dto;

public record LoginResponse(String token, long expiresInSeconds) {}