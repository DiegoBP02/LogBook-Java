package com.dev.logBook.exceptions;

import com.dev.logBook.services.exceptions.ResourceNotFoundException;
import com.dev.logBook.services.exceptions.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> exception(Exception e, HttpServletRequest request) {
        String error = "Server error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError err = new StandardError(Instant.now(), status.value(), error,
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> methodArgumentNotValidException
            (MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Invalid arguments";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final List<String> errors = new ArrayList<>();
        for (final FieldError err : e.getBindingResult().getFieldErrors()) {
            errors.add(err.getField() + ": " + err.getDefaultMessage());
        }

        StandardError err = new StandardError(Instant.now(), status.value(), error,
                errors.toString(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFoundException
            (ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error,
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<StandardError> unauthorizedAccessException
            (UnauthorizedAccessException e, HttpServletRequest request) {
        String error = "Access denied";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(Instant.now(), status.value(), error,
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
