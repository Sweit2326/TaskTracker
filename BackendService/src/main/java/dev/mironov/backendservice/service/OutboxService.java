package dev.mironov.backendservice.service;

import dev.mironov.KafkaCustomHeaders;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final GenericKafkaProducer kafkaProducer;

    @Transactional
    public <T extends KafkaEvent> void create(T payload, String eventType, String source) {
        outboxRepository.save(new OutboxModel(objectMapper.writeValueAsString(payload), eventType, source));
    }

    @Transactional
    public void processAll() {
        processBatch().forEach(this::process);
    }

    @Transactional
    protected List<OutboxModel> processBatch() {
        List<OutboxModel> batch = outboxRepository.findTop10ByStatus(OutboxModel.Status.PENDING);
        batch.forEach(OutboxModel::setProcessing);
        return outboxRepository.saveAll(batch);
    }

    private void process(OutboxModel model) {
        Message<String> message = MessageBuilder
                .withPayload(model.getPayload())
                .setHeader(KafkaHeaders.TOPIC, "EMAIL_SENDING_TASKS")
                .setHeader(KafkaCustomHeaders.EVENT_TYPE, model.getEventType())
                .setHeader(KafkaCustomHeaders.SOURCE, model.getSource())
                .build();
        try {
            kafkaProducer.sendEvent(message);

            model.setSent();
            outboxRepository.save(model);
        } catch (ExecutionException e) {
            processException(e.getCause(), model);
        }
    }

    private void processException(Throwable e, OutboxModel model) {
        // TODO exception classifier
    }
}
