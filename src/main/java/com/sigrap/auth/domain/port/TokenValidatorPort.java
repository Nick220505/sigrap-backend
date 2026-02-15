package com.sigrap.auth.domain.port;

import com.sigrap.auth.domain.model.Email;

/**
 * Port interface for JWT token validation.
 * Defines the contract for validating authentication tokens.
 */
public interface TokenValidatorPort {
  /**
   * Validates a JWT token and extracts the email.
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
