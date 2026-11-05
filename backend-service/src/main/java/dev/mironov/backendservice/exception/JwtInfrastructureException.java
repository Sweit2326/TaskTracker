package dev.mironov.backendservice.exception;

public class JwtInfrastructureException extends RuntimeException {
    public JwtInfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
    public JwtInfrastructureException(String message) {
        super(message);
    }
}
