package com.ticketcompany.booking_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import com.ticketcompany.booking_service.payment.PaymentOutcome;
import com.ticketcompany.booking_service.service.PaymentResultService;

@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);

    private final PaymentResultService paymentResultService;
    private final JsonMapper jsonMapper;

    public PaymentResultListener(PaymentResultService paymentResultService, JsonMapper jsonMapper) {
        this.paymentResultService = paymentResultService;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "payment-results")
    public void onPaymentCompleted(String json) {
        PaymentCompletedEvent result = jsonMapper.readValue(json, PaymentCompletedEvent.class);
        PaymentOutcome outcome = PaymentOutcome.valueOf(result.outcome());

        paymentResultService.applyPaymentResult(result.bookingId(), outcome);
        log.info("Applied payment result {} to booking {}", outcome, result.bookingId());
    }
}