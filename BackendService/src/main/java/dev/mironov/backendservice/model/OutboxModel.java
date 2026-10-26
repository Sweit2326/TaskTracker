package dev.mironov.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 50)
    @Column(name = "last_error_code", length = 50)
    private String lastErrorCode;

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
        this.status = Status.PROCESSING;
        this.createdAt = Instant.now();
        this.processingUntil = Instant.now().plusSeconds(15);
    }

    public void setSent() {
        this.status = Status.SENT;
        this.createdAt = Instant.now();
        this.processingUntil = null;
    }

    // TODO retry status setter
    public void setRetry() {
        this.status = Status.RETRY;
        this.createdAt = Instant.now();
        this.processingUntil = null;
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
        KAFKA_PRODUCER_CLOSED,
        UNKNOWN_PROCESSING_FAILURE
    }
}
