package dev.mironov.backendservice.service;

import dev.mironov.KafkaCustomHeaders;
import dev.mironov.backendservice.kafka.GenericKafkaProducer;
import dev.mironov.backendservice.model.OutboxModel;
import dev.mironov.backendservice.repository.OutboxRepository;
import dev.mironov.event.KafkaEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.common.errors.*;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import javax.net.ssl.SSLHandshakeException;
import java.util.List;

@Slf4j
@Service
@Transactional
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
    public void processAll(List<OutboxModel> batch) {
        batch.forEach(this::process);
    }

    @Transactional
    public List<OutboxModel> processBatch() {
        List<OutboxModel> batch = outboxRepository.findTop10ByStatusOrStatus(OutboxModel.Status.PENDING, OutboxModel.Status.RETRY);
        batch.forEach(OutboxModel::setProcessing);
        batch.removeIf(model -> model.getStatus() == OutboxModel.Status.RETRY);

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
        } catch (Exception e) {
            processException(e.getCause(), model);
        }
    }

    private void processException(Throwable cause, OutboxModel model) {
        switch (cause) {
            case BufferExhaustedException _ -> model.setRetry(OutboxModel.ErrorCode.CLIENT_BUFFER_FULL, cause.getMessage());
            case TimeoutException _ -> model.setRetry(OutboxModel.ErrorCode.NETWORK_TIMEOUT, cause.getMessage());
            case DisconnectException _ -> model.setRetry(OutboxModel.ErrorCode.BROKER_UNAVAILABLE, cause.getMessage());
            case LeaderNotAvailableException _ -> model.setRetry(OutboxModel.ErrorCode.REALLOCATING_PARTITION, cause.getMessage());
            case NotEnoughReplicasException _ -> model.setRetry(OutboxModel.ErrorCode.NOT_ENOUGH_REPLICAS, cause.getMessage());

            case SSLHandshakeException _ -> model.setBlocked(OutboxModel.ErrorCode.AUTHENTICATION_FAILED, cause.getMessage());
            case TopicAuthorizationException _ -> model.setBlocked(OutboxModel.ErrorCode.AUTHORIZATION_DENIED, cause.getMessage());
            case UnknownTopicOrPartitionException _ -> model.setBlocked(OutboxModel.ErrorCode.TOPIC_NOT_FOUND, cause.getMessage());
            case InvalidPartitionsException _ -> model.setBlocked(OutboxModel.ErrorCode.INVALID_PARTITION, cause.getMessage());

            case RecordTooLargeException _ -> model.setDead(OutboxModel.ErrorCode.MESSAGE_TOO_LARGE, cause.getMessage());
            case InvalidDefinitionException _ -> model.setDead(OutboxModel.ErrorCode.JSON_SERIALIZATION_ERROR, cause.getMessage());

            default -> model.setDead(OutboxModel.ErrorCode.UNKNOWN_PROCESSING_FAILURE, cause.getMessage());
        }
        outboxRepository.save(model);
    }
}
