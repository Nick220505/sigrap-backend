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

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all controllers.
 *
 * <p>This handler:
 * <ul>
 *   <li>Converts exceptions to standardized API responses</li>
 *   <li>Handles common exceptions like validation errors, not found errors, etc.</li>
 *   <li>Provides consistent error response format across the API</li>
 *   <li>Includes appropriate HTTP status codes and error messages</li>
 * </ul></p>
 *
 * <p>Error Response Format:
 * <pre>
 * {
 *   "timestamp": "ISO DateTime",
 *   "status": HTTP Status Code,
 *   "error": "Error Type",
 *   "message": "Error Message",
 *   "errors": { // Only for validation errors
 *     "field": "error message"
 *   }
 * }
 * </pre></p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles entity not found exceptions.
   * Returns 404 NOT_FOUND status.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
    EntityNotFoundException ex
  ) {
    return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Handles data integrity violation exceptions.
   * Returns 409 CONFLICT status.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<
    Map<String, Object>
  > handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  /**
   * Handles validation exceptions.
   * Returns 400 BAD_REQUEST status with field-level error details.
   */
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

  /**
   * Handles authentication failures.
   * Returns 401 UNAUTHORIZED status.
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
    BadCredentialsException ex
  ) {
    return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  /**
   * Handles JWT token expiration.
   * Returns 401 UNAUTHORIZED status with specific token expired code.
   */
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

  /**
   * Handles illegal argument exceptions.
   * Returns 409 CONFLICT for email conflicts, 400 BAD_REQUEST for others.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
    IllegalArgumentException ex
  ) {
    if (ex.getMessage().contains("Email already exists")) {
      return createErrorResponse(HttpStatus.CONFLICT, "Email already exists");
    }
    return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handles all other unhandled exceptions.
   * Returns 500 INTERNAL_SERVER_ERROR status.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(
    Exception ex
  ) {
    return createErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ex.getMessage()
    );
  }

  /**
   * Creates a standard error response with the given status and message.
   */
  private ResponseEntity<Map<String, Object>> createErrorResponse(
    HttpStatus status,
    String message
  ) {
    Map<String, Object> errorResponse = createErrorMap(status, message);
    return ResponseEntity.status(status).body(errorResponse);
  }

  /**
   * Creates a standard error map with timestamp and status information.
   */
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
