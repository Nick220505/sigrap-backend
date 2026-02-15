package com.sigrap.auth.domain.port;

import com.sigrap.auth.domain.model.Password;

/**
 * Port interface for password encoding and validation.
 * Defines the contract for secure password operations.
 */
public interface PasswordEncoderPort {
  /**
   * Encodes a plain text password.
   *
   * @param password the password to encode
   * @return the encoded password
   */
  String encode(Password password);

  /**
   * Validates a plain text password against an encoded password.
   *
   * @param password the plain text password
   * @param encodedPassword the encoded password to compare against
   * @return true if the passwords match, false otherwise
   */
  boolean matches(Password password, String encodedPassword);
}
