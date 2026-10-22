package dev.mironov.backendservice.kafka;

import dev.mironov.backendservice.exception.KafkaInfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(Message<?> message) {
        try {
            kafkaTemplate.send(message).get();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
        // TODO: refactor exception handling & add statuses(retry, dead, suspended)
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new KafkaInfrastructureException("Thread was interrupted", e);
            } else {
                throw new KafkaInfrastructureException("Failed to process event", e);
            }
        }
    }
}
