package com.sigrap.auth.domain.port;

import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;

/**
 * Port interface for JWT token generation.
 * Defines the contract for generating authentication tokens.
 */
public interface TokenGeneratorPort {
  /**
   * Generates a JWT token for the given email.
   *
   * @param email the email to generate the token for
   * @return the generated JWT token with expiration time
   */
  JwtToken generateToken(Email email);
}
