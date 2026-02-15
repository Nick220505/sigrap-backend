package com.sigrap.auth.application.port.in;

import com.sigrap.auth.domain.model.Email;

/**
 * Input port for validating JWT tokens.
 * This interface defines the use case for token validation.
 */
public interface ValidateTokenUseCase {
  /**
   * Validates a JWT token and extracts the user email.
   *
   * @param token the JWT token to validate
   * @return the email extracted from the token
   * @throws IllegalArgumentException if the token is invalid or expired
   */
  Email validateToken(String token);

  /**
   * Checks if a token is valid without throwing an exception.
   *
   * @param token the JWT token to check
   * @return true if the token is valid, false otherwise
   */
  boolean isTokenValid(String token);
}
