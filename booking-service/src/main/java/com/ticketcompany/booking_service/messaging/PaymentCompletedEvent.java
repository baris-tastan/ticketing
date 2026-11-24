package com.ticketcompany.booking_service.messaging;

public record PaymentCompletedEvent(Long bookingId, String outcome) {}

