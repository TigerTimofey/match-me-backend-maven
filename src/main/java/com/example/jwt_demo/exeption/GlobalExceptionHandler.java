package com.example.jwt_demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());

        // Default status to 500 Internal Server Error if status is unavailable
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getMessage().contains("404")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("400")) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex.getMessage().contains("401")) {
            status = HttpStatus.UNAUTHORIZED; // Handling unauthorized access specifically
        }

        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", ex.getReason());
        return new ResponseEntity<>(errorDetails, status);
    }
}
