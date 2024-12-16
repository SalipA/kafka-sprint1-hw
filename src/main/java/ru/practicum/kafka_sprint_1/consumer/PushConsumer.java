package ru.practicum.kafka_sprint_1.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_1.config.ConsumerConfiguration;
import ru.practicum.kafka_sprint_1.dto.OrderDto;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/** Класс - реализация push-модели для консьюмера */
@Component
public class PushConsumer implements Consumer {
    private final ConsumerConfiguration consumerConfig;
    private KafkaConsumer<String, OrderDto> consumer;

    private static final String PUSH_CONSUMER_GROUP_ID = "push-consumer-group";
    private static final String ENABLE_AUTO_COMMIT = "true";

    private static final String AUTO_OFFSET_RESET_LATEST = "latest";
    private static final long POLL_DURATION_MS = 5_000;

    public PushConsumer(@Autowired ConsumerConfiguration consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @PostConstruct
    public void initConsumer() {
        this.consumer = new KafkaConsumer<>(getKafkaBrokerProperties());
        consumer.subscribe(Collections.singletonList(consumerConfig.getOrdersTopic()));
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerConfig.getKafkaBootStrapServer());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, PUSH_CONSUMER_GROUP_ID);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_LATEST);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ENABLE_AUTO_COMMIT);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerConfig.getConsumerSessionTimeoutMsConfig());
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, consumerConfig.getJsonDeserializerTrustedPackages());
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, consumerConfig.getJsonDeserializerValueDefaultType());
        return properties;
    }

    public void consumeRecords() {
        try {
            while (true) {
                ConsumerRecords<String, OrderDto> records = consumer.poll(Duration.ofMillis(POLL_DURATION_MS));
                System.out.println("Push-consumer got batch of messages size =  " +  records.count());
                for (ConsumerRecord<String, OrderDto> record : records) {
                    System.out.printf("Push-consumer got message: key = %s, value = %s, offset = %d%n%n",
                        record.key(), record.value(), record.offset());
                }
            }
        } catch (Exception exception){
            System.out.println("An error occurred when push-consumer reading the message: " + exception);
        } finally {
            consumer.close();
        }
    }
}
