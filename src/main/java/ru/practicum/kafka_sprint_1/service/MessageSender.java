package ru.practicum.kafka_sprint_1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_1.config.KafkaConfiguration;
import ru.practicum.kafka_sprint_1.dto.OrderDto;
import ru.practicum.kafka_sprint_1.producer.Producer;

/** Класс, отвечающий за генерацию и отправку сообщений о заказах */
@Service
public class MessageSender {
    private final Producer producer;
    private Long orderCounter = 0L;
    private final AdminService adminService;
    private final KafkaConfiguration kafkaConfiguration;

    public MessageSender (@Autowired Producer producer, @Autowired AdminService adminService,
                          @Autowired KafkaConfiguration kafkaConfiguration) {
        this.producer = producer;
        this.adminService = adminService;
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Scheduled(cron = "${SEND_MESSAGES_SCHEDULE}")
    public void sendMessages() {
        if (!adminService.isTopicExists(kafkaConfiguration.getOrdersTopic())) {
            return;
        }
            var orderDto = generateOrderDto();
            var record = producer.generateRecord(orderDto.getClientId(), orderDto);
            producer.sendRecord(record);
    }

    private OrderDto generateOrderDto () {
        int clientId = (int) (Math.random() * 50) + 1;
        double sum = 100.00 + (Math.random() * (1_000_000.00 - 100.00));
        return OrderDto.builder()
                .orderId(++orderCounter)
                .clientId("client" + clientId)
                .sum(sum)
                .build();
    }
}
