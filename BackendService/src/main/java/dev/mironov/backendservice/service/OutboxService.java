package dev.mironov.backendservice.service;

import dev.mironov.KafkaCustomHeaders;
import dev.mironov.backendservice.exception.KafkaInfrastructureException;
import dev.mironov.backendservice.kafka.GenericKafkaProducer;
import dev.mironov.backendservice.model.OutboxModel;
import dev.mironov.backendservice.repository.OutboxRepository;
import dev.mironov.event.KafkaEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final GenericKafkaProducer kafkaProducer;

    @Transactional
    public <T extends KafkaEvent> void create(T payload, String eventType, String source) {
        OutboxModel outboxLetter = new OutboxModel(objectMapper.writeValueAsString(payload), eventType, source);
        outboxRepository.save(outboxLetter);
    }

    public void processAll() {
        outboxRepository.findAll().forEach(this::process);
    }

    private void process(OutboxModel outboxLetter) {
        Message<String> message = MessageBuilder
                .withPayload(outboxLetter.getPayload())
                .setHeader(KafkaHeaders.TOPIC, "EMAIL_SENDING_TASKS")
                .setHeader(KafkaCustomHeaders.EVENT_TYPE, outboxLetter.getEventType())
                .setHeader(KafkaCustomHeaders.SOURCE, outboxLetter.getSource())
                .build();
        try {
            kafkaProducer.sendEvent(message);
            outboxRepository.delete(outboxLetter);
        } catch (KafkaInfrastructureException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
