package ru.practicum.kafka_sprint_1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Value("${KAFKA_BOOTSTRAP_SERVER}")
    protected String kafkaBootStrapServer;

    @Value("${ORDERS_TOPIC}")
    protected String ordersTopic;

    public String getKafkaBootStrapServer() {
        return kafkaBootStrapServer;
    }

    public String getOrdersTopic() {
        return ordersTopic;
    }

}
