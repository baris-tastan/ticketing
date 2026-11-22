package com.ticketcompany.payment_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ticketcompany.payment_service.bank.FakeBank;

import tools.jackson.databind.json.JsonMapper;

@Component
public class PaymentRequestListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestListener.class);

    private final JsonMapper jsonMapper;
    private final FakeBank fakeBank;
    private final PaymentResultPublisher resultPublisher;

    public PaymentRequestListener(JsonMapper jsonMapper, FakeBank fakeBank,
                                  PaymentResultPublisher resultPublisher) {
        this.jsonMapper = jsonMapper;
        this.fakeBank = fakeBank;
        this.resultPublisher = resultPublisher;
    }

    @KafkaListener(topics = "payment-requests")
    public void onPaymentRequested(String json) {
        PaymentRequestedEvent request = jsonMapper.readValue(json, PaymentRequestedEvent.class);
        log.info("Payment requested for booking {}: {}", request.bookingId(), request.amount());

        String outcome = fakeBank.charge(request.amount(), request.paymentToken());

        resultPublisher.publish(new PaymentCompletedEvent(request.bookingId(), outcome));
    }
}