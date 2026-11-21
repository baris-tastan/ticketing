package com.ticketcompany.booking_service.payment;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(FakePaymentGateway.class);

    @Override
    public PaymentOutcome charge(Long bookingId, BigDecimal amount, String cardNumber) {
        simulateBankDelay();

        String lastFour = cardNumber.length() >= 4
                ? cardNumber.substring(cardNumber.length() - 4)
                : cardNumber;

        PaymentOutcome outcome = lastFour.equals("0002")
                ? PaymentOutcome.DECLINED
                : PaymentOutcome.APPROVED;

        log.info("Charged {} for booking {} (card ending {}): {}", amount, bookingId, lastFour, outcome);
        return outcome;
    }

    private void simulateBankDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}