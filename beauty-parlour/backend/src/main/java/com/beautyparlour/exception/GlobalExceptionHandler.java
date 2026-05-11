package com.beautyparlour.exception;

import com.beautyparlour.model.dto.ErrorResponse;
import com.beautyparlour.model.dto.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleApp(final AppException ex) {
        // Pattern matching for switch (JEP 441) — exhaustive because AppException is sealed
        final HttpStatus status = switch (ex) {
            case AppException.NotFound e     -> e.getStatus();
            case AppException.Conflict e     -> e.getStatus();
            case AppException.Unauthorized e -> e.getStatus();
            case AppException.Forbidden e    -> e.getStatus();
            case AppException.BadRequest e   -> e.getStatus();
        };
        return ResponseEntity.status(status).body(errorBody(ex.getMessage(), status));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(final MethodArgumentNotValidException ex) {
        final var fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "invalid"
                ));
        return ResponseEntity.badRequest().body(
                new ValidationErrorResponse(ZonedDateTime.now().toString(), 400, "Validation failed", fields)
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorBody("Method not allowed: " + ex.getMethod(), HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(final AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorBody("Access denied", HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(final Exception ex) {
        log.error("Unhandled exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponse errorBody(final String message, final HttpStatus status) {
        return new ErrorResponse(ZonedDateTime.now().toString(), status.value(), message);
    }
}
