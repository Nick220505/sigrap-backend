package com.sigrap.auth.application.service;

import com.sigrap.auth.application.port.in.AuthenticateUserUseCase;
import com.sigrap.auth.application.port.in.command.AuthenticateUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;
import com.sigrap.auth.domain.model.Credentials;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import com.sigrap.auth.domain.model.Password;
import com.sigrap.auth.domain.port.TokenGeneratorPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort.UserInfo;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the AuthenticateUserUseCase.
 * This service orchestrates user authentication by:
 * <ul>
 *   <li>Validating credentials format</li>
 *   <li>Authenticating through the user authentication port</li>
 *   <li>Generating a JWT token</li>
 *   <li>Updating last login timestamp</li>
 *   <li>Returning authentication result</li>
 * </ul>
 */
@Service
@Transactional
public class AuthenticateUserService implements AuthenticateUserUseCase {

  private final UserAuthenticationPort userAuthenticationPort;
  private final TokenGeneratorPort tokenGeneratorPort;

  public AuthenticateUserService(
    UserAuthenticationPort userAuthenticationPort,
    TokenGeneratorPort tokenGeneratorPort
  ) {
    this.userAuthenticationPort = userAuthenticationPort;
    this.tokenGeneratorPort = tokenGeneratorPort;
  }

  @Override
  public AuthenticationResult authenticate(AuthenticateUserCommand command) {
    // Create value objects (validates format)
    Email email = new Email(command.email());
    Password password = new Password(command.password());
    Credentials credentials = new Credentials(email, password);

    // Authenticate user (throws exception if authentication fails)
    userAuthenticationPort.authenticate(credentials);

    // Get user information
    UserInfo userInfo = userAuthenticationPort
      .findUserByEmail(email)
      .orElseThrow(() ->
        new IllegalArgumentException("User not found after successful authentication")
      );

    // Generate JWT token
    JwtToken token = tokenGeneratorPort.generateToken(email);

    // Update last login timestamp
    userAuthenticationPort.updateLastLogin(email);

    // Return authentication result
    return new AuthenticationResult(
      token,
      email,
      userInfo.name(),
      LocalDateTime.now(),
      userInfo.role()
    );
  }
}
