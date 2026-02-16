package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import com.sigrap.auth.domain.port.TokenGeneratorPort;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT implementation of the TokenGeneratorPort.
 * This adapter uses the existing JwtUtil to generate JWT tokens.
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

  private final JwtUtil jwtUtil;
  private final long expirationMs;

  /**
   * Constructor for dependency injection.
   *
   * @param jwtUtil the JWT utility for token operations
   * @param expirationMs the token expiration time in milliseconds
   */
  public JwtTokenGeneratorAdapter(
    JwtUtil jwtUtil,
    @Value("${jwt.expiration}") long expirationMs
  ) {
    this.jwtUtil = jwtUtil;
    this.expirationMs = expirationMs;
  }

  /**
   * Generates a JWT token for the given email.
   *
   * @param email the email to generate the token for
   * @return the generated JWT token with expiration time
   */
  @Override
  public JwtToken generateToken(Email email) {
    // Create a UserDetails object for JwtUtil (which expects UserDetails)
    UserDetails userDetails = User.builder()
      .username(email.value())
      .password("") // Not needed for token generation
      .authorities("USER") // Default authority
      .build();

    // Generate token using existing JwtUtil
    String tokenValue = jwtUtil.generateToken(userDetails);

    // Calculate expiration time
    LocalDateTime expiresAt = LocalDateTime.now()
      .plusSeconds(expirationMs / 1000);

    return new JwtToken(tokenValue, expiresAt);
  }
}
