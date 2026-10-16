package dev.mironov.backendservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericKafkaProducer {
    // TODO: KafkaTemplate configuration (maybe in props/config class)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RecordMetadata sendEvent(Message<?> message, boolean isSync) {
        if (isSync) {
            try {
                return kafkaTemplate.send(message).get().getRecordMetadata();
            } catch (ExecutionException | InterruptedException e) {
                log.error("Failed to send event: " + e.getMessage());
                return null; // TODO: exception-handling
            }
        } else {
            CompletableFuture<SendResult<String, Object>> futureResult =
                    kafkaTemplate.send(message);

            futureResult.whenComplete((result, exception) -> {
                if(exception != null) {
                    // TODO: exception-handling
                    log.error("Failed to send event: " + exception.getMessage());
                }
            });
            return null;
        }
    }
}
