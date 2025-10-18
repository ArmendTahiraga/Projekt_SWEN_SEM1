package com.armendtahiraga.App.exceptions;

public class NotJsonBodyException extends RuntimeException {
    public NotJsonBodyException() { }

    public NotJsonBodyException(String message) {
        super(message);
    }

    public NotJsonBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotJsonBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
