package dev.mironov.backendservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericKafkaProducer {
    // TODO: KafkaTemplate configuration (maybe in props/config class)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(Message<?> message) {
        try {
            kafkaTemplate.send(message).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send event: " + e.getMessage());
            // TODO: exception-handling
        }
    }
}
