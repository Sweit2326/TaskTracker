package dev.mironov.backendservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("spring.kafka.bootstrap-servers")
    private String BOOTSTRAP_SERVERS;

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "20000");
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "2000");
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");
        config.put(ProducerConfig.LINGER_MS_CONFIG, "5");
        config.put(ProducerConfig.RETRIES_CONFIG, 5);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
