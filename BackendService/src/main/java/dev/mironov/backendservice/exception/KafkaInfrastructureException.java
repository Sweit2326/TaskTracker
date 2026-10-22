package dev.mironov.backendservice.exception;

public class KafkaInfrastructureException extends RuntimeException {
    public KafkaInfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
