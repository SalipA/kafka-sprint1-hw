package ru.practicum.kafka_sprint_1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_1.config.KafkaConfiguration;
import ru.practicum.kafka_sprint_1.consumer.PullConsumer;
import ru.practicum.kafka_sprint_1.consumer.PushConsumer;
import ru.practicum.kafka_sprint_1.consumer.Consumer;

import java.util.concurrent.CompletableFuture;

/** Класс, отвечающий за запуск консьюмеров */
@Service
public class MessageReader {

    @Value("${TOPIC_SEARCH_INTERVAL_MS}")
    private long searchIntervalMs;
    private final KafkaConfiguration kafkaConfiguration;
    private final PullConsumer pullConsumer;
    private final PushConsumer pushConsumer;
    private final AdminService adminService;

    public MessageReader(@Autowired PullConsumer pullConsumer, @Autowired PushConsumer pushConsumer,
                         @Autowired AdminService adminService, @Autowired KafkaConfiguration kafkaConfiguration) {
        this.pullConsumer = pullConsumer;
        this.pushConsumer = pushConsumer;
        this.adminService = adminService;
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        CompletableFuture.runAsync(() -> consumeWithTopicCheck(pullConsumer));
        CompletableFuture.runAsync(() -> consumeWithTopicCheck(pushConsumer));
    }

    private void consumeWithTopicCheck(Consumer consumer) {
        if (!adminService.isTopicExists(kafkaConfiguration.getOrdersTopic())) {
            adminService.waitForTopic(kafkaConfiguration.getOrdersTopic(), searchIntervalMs);
        }
        consumer.consumeRecords();
    }
}
