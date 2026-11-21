package com.ticketcompany.booking_service.payment;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentOutcome charge(Long bookingId, BigDecimal amount, String cardNumber);
}