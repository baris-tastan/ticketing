package com.ticketcompany.booking_service.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

@Component
public class PaymentRequestPublisher {

    public static final String TOPIC = "payment-requests";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public PaymentRequestPublisher(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void publish(PaymentRequestedEvent event) {
        String key = event.bookingId().toString();
        String json = jsonMapper.writeValueAsString(event);
        kafkaTemplate.send(TOPIC, key, json).join();
    }
}