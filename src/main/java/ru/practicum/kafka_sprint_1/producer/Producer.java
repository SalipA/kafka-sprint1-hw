package ru.practicum.kafka_sprint_1.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_1.config.ProducerConfiguration;
import ru.practicum.kafka_sprint_1.dto.OrderDto;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/** Класс - реализация продюсера */
@Component
public class Producer {

    private KafkaProducer<String, OrderDto> producer;
    private final ProducerConfiguration producerConf;

    public Producer(@Autowired ProducerConfiguration producerConf) {
        this.producerConf = producerConf;
    }

    @PostConstruct
    public void initProducer() {
        this.producer = new KafkaProducer<>(getKafkaBrokerProperties());
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, producerConf.getKafkaBootStrapServer());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        properties.put(ACKS_CONFIG, producerConf.getProducerAcks());
        properties.put(RETRIES_CONFIG, producerConf.getProducerRetriesConfig());
        return properties;
    }

    public ProducerRecord<String, OrderDto> generateRecord(String clientId, OrderDto orderDto) {
        return new ProducerRecord<>(producerConf.getOrdersTopic(), clientId, orderDto);
    }

    public void sendRecord(ProducerRecord<String, OrderDto> record) {
        System.out.println(
            MessageFormat.format("Producer send record. Key: {0}. Value: {1}", record.key(), record.value()));
        producer.send(record,(metadata, exception) -> {
            if (exception != null) {
                System.err.println("Got exception: " + exception.getMessage());
                producer.close();
            }
    });
    }
}
