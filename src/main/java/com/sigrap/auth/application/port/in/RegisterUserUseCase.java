package com.sigrap.auth.application.port.in;

import com.sigrap.auth.application.port.in.command.RegisterUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;

/**
 * Input port for registering a new user.
 * This interface defines the use case for user registration.
 */
public interface RegisterUserUseCase {
  /**
   * Registers a new user with the provided registration data.
   *
   * @param command the command containing user registration data
   * @return the authentication result with JWT token and user information
   * @throws IllegalArgumentException if a user with the same email already exists
   */
  AuthenticationResult register(RegisterUserCommand command);
}
