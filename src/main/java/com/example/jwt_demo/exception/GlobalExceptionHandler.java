package com.example.jwt_demo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

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
            status = HttpStatus.UNAUTHORIZED;
        }

        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", ex.getReason());
        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, Object>> handleClassCastException(ClassCastException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());

        HttpStatus status = HttpStatus.UNAUTHORIZED; // Specifically handle unauthorized access
        errorDetails.put("status", status.value());
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Unauthorized user - invalid or missing Bearer token");

        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        errorDetails.put("status", status.value());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred");

        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", "Resource not found");  // Generic message
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());  // Return 404 instead of 403
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", "Resource not found");  // Generic message
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
