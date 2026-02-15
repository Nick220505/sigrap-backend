package com.sigrap.auth.domain.model;

import java.util.Objects;

/**
 * Value object representing user registration data.
 * Encapsulates all information needed to register a new user.
 */
public record RegistrationData(String name, Email email, Password password) {

  /**
   * Compact constructor with validation.
   *
   * @throws IllegalArgumentException if name is invalid
   * @throws NullPointerException if required fields are null
   */
  public RegistrationData {
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(email, "Email cannot be null");
    Objects.requireNonNull(password, "Password cannot be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
  }
}
