package com.sigrap.auth.application.service;

import com.sigrap.auth.application.port.in.RegisterUserUseCase;
import com.sigrap.auth.application.port.in.command.RegisterUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import com.sigrap.auth.domain.model.Password;
import com.sigrap.auth.domain.model.RegistrationData;
import com.sigrap.auth.domain.port.PasswordEncoderPort;
import com.sigrap.auth.domain.port.TokenGeneratorPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort.UserInfo;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RegisterUserUseCase.
 * This service orchestrates user registration by:
 * <ul>
 *   <li>Validating registration data format</li>
 *   <li>Checking if user already exists</li>
 *   <li>Encoding the password</li>
 *   <li>Creating the user account</li>
 *   <li>Generating a JWT token</li>
 *   <li>Returning authentication result</li>
 * </ul>
 */
@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

  private final UserAuthenticationPort userAuthenticationPort;
  private final PasswordEncoderPort passwordEncoderPort;
  private final TokenGeneratorPort tokenGeneratorPort;

  public RegisterUserService(
    UserAuthenticationPort userAuthenticationPort,
    PasswordEncoderPort passwordEncoderPort,
    TokenGeneratorPort tokenGeneratorPort
  ) {
    this.userAuthenticationPort = userAuthenticationPort;
    this.passwordEncoderPort = passwordEncoderPort;
    this.tokenGeneratorPort = tokenGeneratorPort;
  }

  @Override
  public AuthenticationResult register(RegisterUserCommand command) {
    // Create value objects (validates format)
    Email email = new Email(command.email());
    Password password = new Password(command.password());
    RegistrationData registrationData = new RegistrationData(
      command.name(),
      email,
      password
    );

    // Business rule: email must be unique
    if (userAuthenticationPort.existsByEmail(email)) {
      throw new IllegalArgumentException(
        "User with email '" + email.value() + "' already exists"
      );
    }

    // Encode password
    String encodedPassword = passwordEncoderPort.encode(password);

    // Register user
    UserInfo userInfo = userAuthenticationPort.registerUser(
      registrationData,
      encodedPassword
    );

    // Generate JWT token
    JwtToken token = tokenGeneratorPort.generateToken(email);

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
