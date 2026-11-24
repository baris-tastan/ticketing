package com.ticketcompany.payment_service.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import com.ticketcompany.payment_service.payment.PaymentProcessor;

@Component
public class PaymentRequestListener {

    private final PaymentProcessor paymentProcessor;
    private final JsonMapper jsonMapper;

    public PaymentRequestListener(PaymentProcessor paymentProcessor, JsonMapper jsonMapper) {
        this.paymentProcessor = paymentProcessor;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "payment-requests")
    public void onPaymentRequested(String json) {
        PaymentRequestedEvent request = jsonMapper.readValue(json, PaymentRequestedEvent.class);
        paymentProcessor.process(request);
    }
}