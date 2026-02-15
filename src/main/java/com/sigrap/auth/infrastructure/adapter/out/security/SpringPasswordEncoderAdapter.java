package com.sigrap.auth.infrastructure.adapter.out.security;

import com.sigrap.auth.domain.model.Password;
import com.sigrap.auth.domain.port.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring Security implementation of the PasswordEncoderPort.
 * This adapter uses Spring Security's BCryptPasswordEncoder to encode and validate passwords.
 * 
 * <p>BCrypt is a strong, adaptive hashing algorithm designed for password storage.
 * It includes a salt and is computationally expensive to prevent brute-force attacks.
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class SpringPasswordEncoderAdapter implements PasswordEncoderPort {

  private final BCryptPasswordEncoder encoder;

  /**
   * Constructor that initializes the BCrypt encoder with default strength (10 rounds).
   */
  public SpringPasswordEncoderAdapter() {
    this.encoder = new BCryptPasswordEncoder();
  }

  /**
   * Encodes a plain text password using BCrypt.
   *
   * @param password the password to encode
   * @return the BCrypt encoded password
   */
  @Override
  public String encode(Password password) {
    return encoder.encode(password.value());
  }

  /**
   * Validates a plain text password against an encoded password.
   *
   * @param password the plain text password
   * @param encodedPassword the encoded password to compare against
   * @return true if the passwords match, false otherwise
   */
  @Override
  public boolean matches(Password password, String encodedPassword) {
    return encoder.matches(password.value(), encodedPassword);
  }
}
