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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(Message<?> message) throws ExecutionException {
        try {
            kafkaTemplate.send(message).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("KafkaProducer thread was interrupted while sending message, shutting down...");
        }
    }
}
