package com.sigrap.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing a JWT authentication token.
 * Encapsulates the token value and its expiration time.
 */
public record JwtToken(String value, LocalDateTime expiresAt) {

  /**
   * Compact constructor with validation.
   *
   * @throws IllegalArgumentException if token value is invalid
   * @throws NullPointerException if required fields are null
   */
  public JwtToken {
    Objects.requireNonNull(value, "Token value cannot be null");
    Objects.requireNonNull(expiresAt, "Expiration time cannot be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException("Token value cannot be blank");
    }
  }

  /**
   * Checks if the token has expired.
   *
   * @return true if the token has expired, false otherwise
   */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  /**
   * Checks if the token is still valid (not expired).
   *
   * @return true if the token is valid, false otherwise
   */
  public boolean isValid() {
    return !isExpired();
  }
}
