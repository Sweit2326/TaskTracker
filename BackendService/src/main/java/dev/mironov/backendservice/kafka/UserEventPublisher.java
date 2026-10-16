package dev.mironov.backendservice.kafka;

import dev.mironov.KafkaCustomHeaders;
import dev.mironov.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    private final GenericKafkaProducer kafkaProducer;

    public void sendUserRegistered(UserRegisteredEvent event) {
        Message<UserRegisteredEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "user-registered-events-topic")
                .setHeader(KafkaCustomHeaders.EVENT_TYPE, "userRegistered")
                .setHeader(KafkaCustomHeaders.SOURCE, "register-service")
                .build();
        kafkaProducer.sendEvent(message, true);
    }
}
