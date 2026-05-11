package com.beautyparlour.exception;

import org.springframework.http.HttpStatus;

public abstract sealed class AppException extends RuntimeException
        permits AppException.NotFound, AppException.Conflict,
                AppException.Unauthorized, AppException.Forbidden,
                AppException.BadRequest {

    AppException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

    public static final class NotFound extends AppException {
        public NotFound(String message) { super(message); }
        @Override public HttpStatus getStatus() { return HttpStatus.NOT_FOUND; }
    }

    public static final class Conflict extends AppException {
        public Conflict(String message) { super(message); }
        @Override public HttpStatus getStatus() { return HttpStatus.CONFLICT; }
    }

    public static final class Unauthorized extends AppException {
        public Unauthorized(String message) { super(message); }
        @Override public HttpStatus getStatus() { return HttpStatus.UNAUTHORIZED; }
    }

    public static final class Forbidden extends AppException {
        public Forbidden(String message) { super(message); }
        @Override public HttpStatus getStatus() { return HttpStatus.FORBIDDEN; }
    }

    public static final class BadRequest extends AppException {
        public BadRequest(String message) { super(message); }
        @Override public HttpStatus getStatus() { return HttpStatus.BAD_REQUEST; }
    }
}
