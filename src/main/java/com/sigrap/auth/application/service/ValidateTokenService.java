package com.sigrap.auth.application.service;

import com.sigrap.auth.application.port.in.ValidateTokenUseCase;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.port.TokenValidatorPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the ValidateTokenUseCase.
 * This service orchestrates token validation by delegating to the token validator port.
 */
@Service
@Transactional(readOnly = true)
public class ValidateTokenService implements ValidateTokenUseCase {

  private final TokenValidatorPort tokenValidatorPort;

  public ValidateTokenService(TokenValidatorPort tokenValidatorPort) {
    this.tokenValidatorPort = tokenValidatorPort;
  }

  @Override
  public Email validateToken(String token) {
    return tokenValidatorPort.validateToken(token);
  }

  @Override
  public boolean isTokenValid(String token) {
    return tokenValidatorPort.isTokenValid(token);
  }
}
