package ru.practicum.kafka_sprint_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class KafkaSprint1App {
    public static void main(String[] args) {
        SpringApplication.run(KafkaSprint1App.class, args);
    }
}
