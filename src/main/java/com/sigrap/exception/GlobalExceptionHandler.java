package com.sigrap.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
    EntityNotFoundException ex
  ) {
    return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<
    Map<String, Object>
  > handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
    MethodArgumentNotValidException ex
  ) {
    Map<String, Object> errorResponse = createErrorMap(
      HttpStatus.BAD_REQUEST,
      "Validation failed"
    );
    Map<String, String> validationErrors = new HashMap<>();

    ex
      .getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        validationErrors.put(fieldName, errorMessage);
      });

    errorResponse.put("errors", validationErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
    BadCredentialsException ex
  ) {
    return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<Map<String, Object>> handleExpiredJwtException(
    ExpiredJwtException ex
  ) {
    Map<String, Object> errorResponse = createErrorMap(
      HttpStatus.UNAUTHORIZED,
      "Token has expired"
    );
    errorResponse.put("code", "TOKEN_EXPIRED");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
    IllegalArgumentException ex
  ) {
    if (ex.getMessage().contains("Email already exists")) {
      return createErrorResponse(HttpStatus.CONFLICT, "Email already exists");
    }
    return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(
    Exception ex
  ) {
    return createErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ex.getMessage()
    );
  }

  private ResponseEntity<Map<String, Object>> createErrorResponse(
    HttpStatus status,
    String message
  ) {
    Map<String, Object> errorResponse = createErrorMap(status, message);
    return ResponseEntity.status(status).body(errorResponse);
  }

  private Map<String, Object> createErrorMap(
    HttpStatus status,
    String message
  ) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", status.value());
    errorResponse.put("error", status.getReasonPhrase());
    errorResponse.put("message", message);
    return errorResponse;
  }
}
