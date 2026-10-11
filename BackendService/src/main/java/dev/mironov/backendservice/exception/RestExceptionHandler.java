package dev.mironov.backendservice.exception;

import dev.mironov.backendservice.record.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    private final String ERROR_TYPE = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    @ExceptionHandler(value = EmailAlreadyTakenException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyTaken(EmailAlreadyTakenException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidJwtTokenException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleInvalidJwtToken(InvalidJwtTokenException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = JwtInfrastructureException.class, produces = ERROR_TYPE)
    public ResponseEntity<ErrorResponse> handleJwtInfrastructureException(JwtInfrastructureException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
