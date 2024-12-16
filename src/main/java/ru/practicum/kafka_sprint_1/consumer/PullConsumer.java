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

/** Класс - реализация pull-модели для консьюмера */
@Component
public class PullConsumer implements Consumer {
    private final ConsumerConfiguration consumerConfig;
    private KafkaConsumer<String, OrderDto> consumer;

    private static final String PULL_CONSUMER_GROUP_ID = "pull-consumer-group";
    private static final String ENABLE_AUTO_COMMIT = "false";
    private static final String AUTO_OFFSET_RESET_EARLIEST = "earliest";
    private static final long POLL_DURATION_MS = 60_000;
    private static final int FETCH_MIN_BYTES = 1_024;
    private static final int FETCH_MAX_WAIT_MS = 60_000;
    private static final int REQUEST_TIMEOUT_MS = 100_000;

    public PullConsumer(@Autowired ConsumerConfiguration consumerConfig) {
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
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, PULL_CONSUMER_GROUP_ID);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ENABLE_AUTO_COMMIT);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_EARLIEST);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerConfig.getConsumerSessionTimeoutMsConfig());
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, FETCH_MIN_BYTES);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, FETCH_MAX_WAIT_MS);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, consumerConfig.getJsonDeserializerTrustedPackages());
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, consumerConfig.getJsonDeserializerValueDefaultType());
        return properties;
    }
    public void consumeRecords() {
        try {
            while (true) {
                ConsumerRecords<String, OrderDto> records = consumer.poll(Duration.ofMillis(POLL_DURATION_MS));
                System.out.println("Pull-consumer got batch of messages size =  " +  records.count());
                for (ConsumerRecord<String, OrderDto> record : records) {
                    System.out.printf("Pull-consumer got message: key = %s, value = %s, offset = %d%n%n",
                        record.key(), record.value(), record.offset());
                }
                try {
                    consumer.commitSync();
                } catch (Exception e) {
                    System.out.println("An error occurred during the offset commit process: " + e.getMessage());
                }
            }
        } catch (Exception exception){
            System.out.println("An error occurred while reading the message: " + exception);
        } finally {
            consumer.close();
        }
    }
}
