package com.sigrap.auth.application.port.in;

import com.sigrap.auth.application.port.in.command.AuthenticateUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;

/**
 * Input port for authenticating a user.
 * This interface defines the use case for user authentication.
 */
public interface AuthenticateUserUseCase {
  /**
   * Authenticates a user with the provided credentials.
   *
   * @param command the command containing authentication credentials
   * @return the authentication result with JWT token and user information
   * @throws IllegalArgumentException if authentication fails
   */
  AuthenticationResult authenticate(AuthenticateUserCommand command);
}
