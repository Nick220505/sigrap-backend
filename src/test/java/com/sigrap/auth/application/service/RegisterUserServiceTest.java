package com.sigrap.auth.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.sigrap.auth.application.port.in.command.RegisterUserCommand;
import com.sigrap.auth.domain.model.AuthenticationResult;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import com.sigrap.auth.domain.port.PasswordEncoderPort;
import com.sigrap.auth.domain.port.TokenGeneratorPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort.UserInfo;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

  @Mock
  private UserAuthenticationPort userAuthenticationPort;

  @Mock
  private PasswordEncoderPort passwordEncoderPort;

  @Mock
  private TokenGeneratorPort tokenGeneratorPort;

  @InjectMocks
  private RegisterUserService registerUserService;

  @Test
  void shouldRegisterUserSuccessfully() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      "John Doe",
      "john@example.com",
      "Password123!"
    );

    Email email = new Email("john@example.com");
    UserInfo userInfo = new UserInfo(
      email,
      "John Doe",
      "encodedPassword",
      LocalDateTime.now(),
      "USER"
    );

    JwtToken token = new JwtToken("jwt-token-value", LocalDateTime.now().plusHours(1));

    when(userAuthenticationPort.existsByEmail(any())).thenReturn(false);
    when(passwordEncoderPort.encode(any())).thenReturn("encodedPassword");
    when(userAuthenticationPort.registerUser(any(), anyString())).thenReturn(userInfo);
    when(tokenGeneratorPort.generateToken(any())).thenReturn(token);

    // When
    AuthenticationResult result = registerUserService.register(command);

    // Then
    assertNotNull(result);
    assertEquals(token, result.getToken());
    assertEquals(email, result.getEmail());
    assertEquals("John Doe", result.getName());
    assertEquals("USER", result.getRole());
    assertNotNull(result.getLastLogin());

    verify(userAuthenticationPort).existsByEmail(email);
    verify(passwordEncoderPort).encode(any());
    verify(userAuthenticationPort).registerUser(any(), eq("encodedPassword"));
    verify(tokenGeneratorPort).generateToken(email);
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      "John Doe",
      "existing@example.com",
      "Password123!"
    );

    when(userAuthenticationPort.existsByEmail(any())).thenReturn(true);

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> registerUserService.register(command)
    );

    assertTrue(exception.getMessage().contains("already exists"));

    verify(userAuthenticationPort).existsByEmail(any());
    verify(passwordEncoderPort, never()).encode(any());
    verify(userAuthenticationPort, never()).registerUser(any(), anyString());
    verify(tokenGeneratorPort, never()).generateToken(any());
  }

  @Test
  void shouldThrowExceptionForInvalidEmailFormat() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      "John Doe",
      "invalid-email",
      "Password123!"
    );

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> registerUserService.register(command)
    );

    verify(userAuthenticationPort, never()).existsByEmail(any());
  }

  @Test
  void shouldThrowExceptionForInvalidPasswordFormat() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      "John Doe",
      "john@example.com",
      "weak"
    );

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> registerUserService.register(command)
    );

    verify(userAuthenticationPort, never()).existsByEmail(any());
  }

  @Test
  void shouldThrowExceptionForBlankName() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      "",
      "john@example.com",
      "Password123!"
    );

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> registerUserService.register(command)
    );

    verify(userAuthenticationPort, never()).existsByEmail(any());
  }

  @Test
  void shouldThrowExceptionForNullName() {
    // Given
    RegisterUserCommand command = new RegisterUserCommand(
      null,
      "john@example.com",
      "Password123!"
    );

    // When/Then
    assertThrows(
      NullPointerException.class,
      () -> registerUserService.register(command)
    );

    verify(userAuthenticationPort, never()).existsByEmail(any());
  }
}
