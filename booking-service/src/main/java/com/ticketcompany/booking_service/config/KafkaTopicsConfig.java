package com.ticketcompany.booking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.ticketcompany.booking_service.messaging.PaymentRequestPublisher;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic paymentRequestsTopic() {
        return TopicBuilder.name(PaymentRequestPublisher.TOPIC).partitions(3).build();
    }
}