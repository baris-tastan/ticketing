package com.ticketcompany.payment_service.bank;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FakeBank {

    private static final Logger log = LoggerFactory.getLogger(FakeBank.class);

    public String charge(BigDecimal amount, String paymentToken) {
        simulateBankDelay();
        String outcome = paymentToken.endsWith("0002") ? "DECLINED" : "APPROVED";
        log.info("Charged {} with {}: {}", amount, paymentToken, outcome);
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