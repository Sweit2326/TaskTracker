package dev.mironov.backendservice.kafka;

import dev.mironov.KafkaCustomHeaders;
import dev.mironov.event.EmailSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    private final GenericKafkaProducer kafkaProducer;

    public void sendRegisterEvent(EmailSentEvent event) {
        Message<EmailSentEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "EMAIL_SENDING_TASKS")
                .setHeader(KafkaCustomHeaders.EVENT_TYPE, "userRegistered")
                .setHeader(KafkaCustomHeaders.SOURCE, "register-service")
                .build();
        kafkaProducer.sendEvent(message);
    }
}
