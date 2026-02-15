package com.sigrap.auth.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a password.
 * Validates password strength requirements at the domain level.
 *
 * <p>Password requirements:
 * <ul>
 *   <li>Minimum 8 characters</li>
 *   <li>At least one uppercase letter</li>
 *   <li>At least one lowercase letter</li>
 *   <li>At least one number</li>
 *   <li>At least one special character (@$!%*?&)</li>
 * </ul></p>
 */
public record Password(String value) {

  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
  );

  /**
   * Compact constructor with validation.
   *
   * @throws IllegalArgumentException if password doesn't meet requirements
   */
  public Password {
    Objects.requireNonNull(value, "Password cannot be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException("Password cannot be blank");
    }
    if (!PASSWORD_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException(
        "Password must be at least 8 characters long and contain at least one uppercase letter, " +
        "one lowercase letter, one number, and one special character"
      );
    }
  }
}
