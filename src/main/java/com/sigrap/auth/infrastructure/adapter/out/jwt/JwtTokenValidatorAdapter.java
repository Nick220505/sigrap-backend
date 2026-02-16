package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.port.TokenValidatorPort;
import org.springframework.stereotype.Component;

/**
 * JWT implementation of the TokenValidatorPort.
 * This adapter uses the existing JwtUtil to validate JWT tokens.
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class JwtTokenValidatorAdapter implements TokenValidatorPort {

  private final JwtUtil jwtUtil;

  /**
   * Constructor for dependency injection.
   *
   * @param jwtUtil the JWT utility for token operations
   */
  public JwtTokenValidatorAdapter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Validates a JWT token and extracts the email.
   * 
   * <p>This method validates the token signature and expiration through JwtUtil.
   * The extractUsername method will throw an exception if the token is invalid or expired.
   *
   * @param token the JWT token to validate
   * @return the email extracted from the token
   * @throws IllegalArgumentException if the token is invalid or expired
   */
  @Override
  public Email validateToken(String token) {
    try {
      // extractUsername will throw an exception if token is invalid or expired
      String email = jwtUtil.extractUsername(token);
      if (email == null || email.isBlank()) {
        throw new IllegalArgumentException("Token does not contain a valid email");
      }
      return new Email(email);
    } catch (IllegalArgumentException e) {
      // Re-throw our own exceptions
      throw e;
    } catch (Exception e) {
      // Wrap JWT library exceptions
      throw new IllegalArgumentException("Invalid or expired token: " + e.getMessage(), e);
    }
  }

  /**
   * Checks if a token is valid without throwing an exception.
   *
   * @param token the JWT token to check
   * @return true if the token is valid, false otherwise
   */
  @Override
  public boolean isTokenValid(String token) {
    try {
      String email = jwtUtil.extractUsername(token);
      return email != null && !email.isBlank();
    } catch (Exception e) {
      return false;
    }
  }
}
