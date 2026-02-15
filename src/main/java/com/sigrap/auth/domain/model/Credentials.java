package com.sigrap.auth.domain.model;

import java.util.Objects;

/**
 * Value object representing user credentials for authentication.
 * Encapsulates email and password as a single authentication unit.
 */
public record Credentials(Email email, Password password) {

  /**
   * Compact constructor with validation.
   *
   * @throws NullPointerException if email or password is null
   */
  public Credentials {
    Objects.requireNonNull(email, "Email cannot be null");
    Objects.requireNonNull(password, "Password cannot be null");
  }
}
