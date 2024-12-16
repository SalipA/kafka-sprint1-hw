package ru.practicum.kafka_sprint_1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.practicum.kafka_sprint_1.dto.OrderDto;

@Configuration
public class ConsumerConfiguration extends KafkaConfiguration {

    private final String jsonDeserializerTrustedPackages = "ru.practicum.kafka_sprint_1.dto";

    private final String jsonDeserializerValueDefaultType = OrderDto.class.getName();

    @Value("${SESSION_TIMEOUT_MS_CONFIG}")
    private int consumerSessionTimeoutMsConfig;

    public int getConsumerSessionTimeoutMsConfig() {
        return consumerSessionTimeoutMsConfig;
    }

    public String getJsonDeserializerTrustedPackages() {
        return jsonDeserializerTrustedPackages;
    }

    public String getJsonDeserializerValueDefaultType() {
        return jsonDeserializerValueDefaultType;
    }
}
