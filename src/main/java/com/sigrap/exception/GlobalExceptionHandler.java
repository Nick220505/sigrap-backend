package com.sigrap.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
    return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> errorResponse = createErrorMap(HttpStatus.BAD_REQUEST, "Validation failed");
    Map<String, String> validationErrors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });

    errorResponse.put("errors", validationErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
    Map<String, Object> errorResponse = createErrorMap(status, message);
    return ResponseEntity.status(status).body(errorResponse);
  }

  private Map<String, Object> createErrorMap(HttpStatus status, String message) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", status.value());
    errorResponse.put("error", status.getReasonPhrase());
    errorResponse.put("message", message);
    return errorResponse;
  }
}