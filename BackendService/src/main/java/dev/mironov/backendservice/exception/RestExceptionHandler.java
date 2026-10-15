package dev.mironov.backendservice.exception;

import dev.mironov.backendservice.record.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    private final String ERROR_TYPE = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @ExceptionHandler(value = EmailAlreadyTakenException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyTakenException(EmailAlreadyTakenException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidJwtTokenException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleInvalidJwtTokenException(InvalidJwtTokenException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = JwtInfrastructureException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleJwtInfrastructureException(JwtInfrastructureException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = TaskNotFoundException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ConstraintViolationException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getConstraintViolations().iterator().next().getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = ERROR_TYPE)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponse> responses = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            responses.add(new ErrorResponse(errorMessage));
        });
        return new ResponseEntity<>(responses, HttpStatus.BAD_REQUEST);
    }
}
