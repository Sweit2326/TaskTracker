package dev.mironov.backendservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic createRegistrationTopic() {
        return TopicBuilder.name("user-registered-events-topic")
                .partitions(3)
                .replicas(3)
                .build();
    }
}
