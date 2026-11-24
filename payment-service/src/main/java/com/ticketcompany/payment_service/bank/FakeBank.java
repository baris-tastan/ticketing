package com.ticketcompany.payment_service.bank;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FakeBank {

    private static final Logger log = LoggerFactory.getLogger(FakeBank.class);

    private final Map<Long, String> resultsByKey = new ConcurrentHashMap<>();

    public String charge(Long idempotencyKey, BigDecimal amount, String paymentToken) {
        return resultsByKey.computeIfAbsent(idempotencyKey, key -> reallyCharge(key, amount, paymentToken));
    }

    private String reallyCharge(Long key, BigDecimal amount, String paymentToken) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String result = paymentToken.endsWith("0002") ? "DECLINED" : "APPROVED";
        log.info("BANK: charged {} for key {} -> {}", amount, key, result);
        return result;
    }
}