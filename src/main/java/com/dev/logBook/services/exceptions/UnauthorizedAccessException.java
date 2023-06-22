package com.dev.logBook.services.exceptions;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String msg) {
        super(msg);
    }
}
