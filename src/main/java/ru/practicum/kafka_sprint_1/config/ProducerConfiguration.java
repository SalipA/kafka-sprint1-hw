package ru.practicum.kafka_sprint_1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfiguration extends KafkaConfiguration {

    @Value("${ACKS_CONFIG}")
    private String producerAcks;

    @Value("${RETRIES_CONFIG}")
    private String producerRetriesConfig;

    public String getProducerAcks() {
        return producerAcks;
    }

    public String getProducerRetriesConfig() {
        return producerRetriesConfig;
    }

}
