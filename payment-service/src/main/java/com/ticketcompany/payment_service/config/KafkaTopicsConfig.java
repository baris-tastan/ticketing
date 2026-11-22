package com.ticketcompany.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.ticketcompany.payment_service.messaging.PaymentResultPublisher;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic paymentResultsTopic() {
        return TopicBuilder.name(PaymentResultPublisher.TOPIC).partitions(3).build();
    }
}