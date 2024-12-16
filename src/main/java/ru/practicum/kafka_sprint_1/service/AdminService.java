package ru.practicum.kafka_sprint_1.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_1.config.KafkaConfiguration;

import javax.annotation.PostConstruct;
import java.util.Properties;

/** Класс, отвечающий за ожидание создания топика */
@Service
public class AdminService {

    private final KafkaConfiguration kafkaConfiguration;
    private AdminClient adminClient;


    public AdminService(@Autowired KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @PostConstruct
    public void initAdminClient() {
        this.adminClient = AdminClient.create(getKafkaBrokerProperties());
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getKafkaBootStrapServer());
        return properties;
    }


    public boolean isTopicExists(String topicName) {
        try {
            return adminClient.listTopics().names().get().contains(topicName);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while checking for topic availability: " + e.getMessage());
        }
    }

    public void waitForTopic(String topicName, long searchIntervalMs) {
        while (true) {
            if (isTopicExists(topicName)) {
                System.out.println(String.format("Topic: %s was found", topicName));
                break;
            }
            System.out.println(String.format("Topic: %s wan not found. Next try after %s min... ", topicName,
                searchIntervalMs / 1000 / 60));
            try {
                Thread.sleep(searchIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Waiting for topic creation was interrupted: " + e.getMessage());
            }
        }
    }
}
