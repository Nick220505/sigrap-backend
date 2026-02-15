package com.sigrap.auth.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an email address.
 * Ensures email format validation at the domain level.
 */
public record Email(String value) {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
  );

  /**
   * Compact constructor with validation.
   *
   * @throws IllegalArgumentException if email is invalid
   */
  public Email {
    Objects.requireNonNull(value, "Email cannot be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException("Email cannot be blank");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format: " + value);
    }
  }
}
