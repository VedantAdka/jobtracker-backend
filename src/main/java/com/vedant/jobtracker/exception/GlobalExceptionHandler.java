package com.vedant.jobtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Catches exceptions thrown from ANY controller, in one place, instead of
// repeating try/catch blocks everywhere. This also cleans up the raw
// validation error responses you'd otherwise get by default.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // Thrown by ResumeService for a bad file (not a PDF, empty, unreadable,
    // or no extractable text) - reuses the same centralized handling instead
    // of a try/catch in the controller.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    // Thrown by AiAnalysisService - not configured, unreachable, or an
    // unparseable response. 502 because the failure is upstream (Gemini),
    // not something wrong with the request itself.
    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<Map<String, String>> handleAiServiceError(AiServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                errors.put(fe.getField(), fe.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
