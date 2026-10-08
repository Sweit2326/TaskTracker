package dev.mironov.backendservice.exception;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException() {
        super("This email is already taken");
    }
}
