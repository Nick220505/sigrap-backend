package com.sigrap.auth.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sigrap.auth.application.port.in.command.AuthenticateUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import com.sigrap.auth.domain.port.TokenGeneratorPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort.UserInfo;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

  @Mock
  private UserAuthenticationPort userAuthenticationPort;

  @Mock
  private TokenGeneratorPort tokenGeneratorPort;

  @InjectMocks
  private AuthenticateUserService authenticateUserService;

  @Test
  void shouldAuthenticateUserSuccessfully() {
    // Given
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      "user@example.com",
      "Password123!"
    );

    Email email = new Email("user@example.com");
    UserInfo userInfo = new UserInfo(
      email,
      "John Doe",
      "encodedPassword",
      LocalDateTime.now().minusDays(1),
      "USER"
    );

    JwtToken token = new JwtToken("jwt-token-value", LocalDateTime.now().plusHours(1));

    doNothing().when(userAuthenticationPort).authenticate(any());
    when(userAuthenticationPort.findUserByEmail(any())).thenReturn(Optional.of(userInfo));
    when(tokenGeneratorPort.generateToken(any())).thenReturn(token);
    doNothing().when(userAuthenticationPort).updateLastLogin(any());

    // When
    AuthenticationResult result = authenticateUserService.authenticate(command);

    // Then
    assertNotNull(result);
    assertEquals(token, result.getToken());
    assertEquals(email, result.getEmail());
    assertEquals("John Doe", result.getName());
    assertEquals("USER", result.getRole());
    assertNotNull(result.getLastLogin());

    verify(userAuthenticationPort).authenticate(any());
    verify(userAuthenticationPort).findUserByEmail(email);
    verify(tokenGeneratorPort).generateToken(email);
    verify(userAuthenticationPort).updateLastLogin(email);
  }

  @Test
  void shouldThrowExceptionWhenAuthenticationFails() {
    // Given
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      "user@example.com",
      "WrongPassword1!"
    );

    doThrow(new IllegalArgumentException("Invalid credentials"))
      .when(userAuthenticationPort)
      .authenticate(any());

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> authenticateUserService.authenticate(command)
    );

    verify(userAuthenticationPort).authenticate(any());
    verify(userAuthenticationPort, never()).findUserByEmail(any());
    verify(tokenGeneratorPort, never()).generateToken(any());
    verify(userAuthenticationPort, never()).updateLastLogin(any());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFoundAfterAuthentication() {
    // Given
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      "user@example.com",
      "Password123!"
    );

    doNothing().when(userAuthenticationPort).authenticate(any());
    when(userAuthenticationPort.findUserByEmail(any())).thenReturn(Optional.empty());

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> authenticateUserService.authenticate(command)
    );

    assertEquals("User not found after successful authentication", exception.getMessage());

    verify(userAuthenticationPort).authenticate(any());
    verify(userAuthenticationPort).findUserByEmail(any());
    verify(tokenGeneratorPort, never()).generateToken(any());
    verify(userAuthenticationPort, never()).updateLastLogin(any());
  }

  @Test
  void shouldThrowExceptionForInvalidEmailFormat() {
    // Given
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      "invalid-email",
      "Password123!"
    );

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> authenticateUserService.authenticate(command)
    );

    verify(userAuthenticationPort, never()).authenticate(any());
  }

  @Test
  void shouldThrowExceptionForInvalidPasswordFormat() {
    // Given
    AuthenticateUserCommand command = new AuthenticateUserCommand(
      "user@example.com",
      "weak"
    );

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> authenticateUserService.authenticate(command)
    );

    verify(userAuthenticationPort, never()).authenticate(any());
  }
}
