package com.ticketcompany.booking_service.messaging;

import java.math.BigDecimal;

public record PaymentRequestedEvent(Long bookingId, BigDecimal amount, String paymentToken) {}