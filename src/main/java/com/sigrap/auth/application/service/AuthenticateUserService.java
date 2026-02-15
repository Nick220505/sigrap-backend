package com.sigrap.auth.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.UserLoginEvent;
import com.sigrap.audit.domain.event.UserLoginFailedEvent;
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

@Service
@Transactional
public class AuthenticateUserService implements AuthenticateUserUseCase {

  private final UserAuthenticationPort userAuthenticationPort;
  private final TokenGeneratorPort tokenGeneratorPort;
  private final EventPublisherPort eventPublisher;

  public AuthenticateUserService(
    UserAuthenticationPort userAuthenticationPort,
    TokenGeneratorPort tokenGeneratorPort,
    EventPublisherPort eventPublisher
  ) {
    this.userAuthenticationPort = userAuthenticationPort;
    this.tokenGeneratorPort = tokenGeneratorPort;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public AuthenticationResult authenticate(AuthenticateUserCommand command) {
    long startTime = System.currentTimeMillis();
    
    Email email = new Email(command.email());
    Password password = new Password(command.password());
    Credentials credentials = new Credentials(email, password);

    try {
      userAuthenticationPort.authenticate(credentials);

      UserInfo userInfo = userAuthenticationPort
        .findUserByEmail(email)
        .orElseThrow(() ->
          new IllegalArgumentException("User not found after successful authentication")
        );

      JwtToken token = tokenGeneratorPort.generateToken(email);

      userAuthenticationPort.updateLastLogin(email);

      long durationMs = System.currentTimeMillis() - startTime;
      eventPublisher.publish(new UserLoginEvent(
        email.value(),
        LocalDateTime.now(),
        null,
        null,
        durationMs
      ));

      return new AuthenticationResult(
        token,
        email,
        userInfo.name(),
        LocalDateTime.now(),
        userInfo.role()
      );
    } catch (Exception e) {
      long durationMs = System.currentTimeMillis() - startTime;
      eventPublisher.publish(new UserLoginFailedEvent(
        email.value(),
        e.getMessage(),
        LocalDateTime.now(),
        null,
        null,
        durationMs
      ));
      throw e;
    }
  }
}
