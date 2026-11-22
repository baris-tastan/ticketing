package com.ticketcompany.payment_service.messaging;

public record PaymentCompletedEvent(Long bookingId, String outcome) {}