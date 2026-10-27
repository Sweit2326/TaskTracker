package dev.mironov.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "Outbox")
@NoArgsConstructor
public class OutboxModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @NotBlank
    private String payload;

    @Getter
    @NotBlank
    @Column(name = "event_type")
    private String eventType;

    @Getter
    @NotBlank
    private String source;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @Column(name = "attempts_count")
    private Integer attemptsCount;

    @Getter
    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Getter
    @Column(name = "processing_until", nullable = false)
    private Instant processingUntil;

    @Column(name = "last_error_code", length = 50)
    @Enumerated(EnumType.STRING)
    private ErrorCode lastErrorCode;

    @Lob
    @Column(name = "last_error_message")
    private String lastErrorMessage;

    public OutboxModel(String payload, String eventType, String source) {
        this.payload = payload;
        this.eventType = eventType;
        this.source = source;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
    }

    public void setProcessing() {
        if (status == Status.RETRY && nextAttemptAt.isBefore(Instant.now())) {
            nextAttemptAt = null;
        } else if (nextAttemptAt != null && nextAttemptAt.isAfter(Instant.now())) return;

        status = Status.PROCESSING;
        createdAt = Instant.now();
        processingUntil = Instant.now().plusSeconds(15);
    }

    public void setSent() {
        status = Status.SENT;
        createdAt = Instant.now();
        processingUntil = null;
    }

    public void setRetry(ErrorCode code, String message) {
        createdAt = Instant.now();
        processingUntil = null;
        lastErrorCode = code;
        lastErrorMessage = message;

        attemptsCount = (attemptsCount == null) ? 1 : attemptsCount+1;

        if (attemptsCount < 4) {
            status = Status.RETRY;
            nextAttemptAt = Instant.now().plusSeconds(20L *attemptsCount);
        } else status = Status.DEAD;
    }

    public void setBlocked(ErrorCode code, String message) {
        status = Status.BLOCKED;
        createdAt = Instant.now();
        processingUntil = null;
        lastErrorCode = code;
        lastErrorMessage = message;
    }

    public void setDead(ErrorCode code, String message) {
        status = Status.DEAD;
        createdAt = Instant.now();
        processingUntil = null;
        lastErrorCode = code;
        lastErrorMessage = message;
    }

    public enum Status {
        PENDING,
        PROCESSING,
        RETRY,
        SENT,
        DEAD,
        BLOCKED
    }

    public enum ErrorCode {
        // RETRY:
        NETWORK_TIMEOUT,
        BROKER_UNAVAILABLE,
        REALLOCATING_PARTITION,
        NOT_ENOUGH_REPLICAS,
        CLIENT_BUFFER_FULL,
        // BLOCKED:
        AUTHENTICATION_FAILED,
        AUTHORIZATION_DENIED,
        TOPIC_NOT_FOUND,
        INVALID_PARTITION,
        // DEAD:
        MESSAGE_TOO_LARGE,
        JSON_SERIALIZATION_ERROR,
        // UNKNOWN:
        UNKNOWN_PROCESSING_FAILURE
    }
}
