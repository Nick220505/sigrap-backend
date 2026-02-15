package com.sigrap.auth.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.port.TokenValidatorPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateTokenServiceTest {

  @Mock
  private TokenValidatorPort tokenValidatorPort;

  @InjectMocks
  private ValidateTokenService validateTokenService;

  @Test
  void shouldValidateTokenSuccessfully() {
    // Given
    String token = "valid-jwt-token";
    Email expectedEmail = new Email("user@example.com");

    when(tokenValidatorPort.validateToken(token)).thenReturn(expectedEmail);

    // When
    Email result = validateTokenService.validateToken(token);

    // Then
    assertNotNull(result);
    assertEquals(expectedEmail, result);
    verify(tokenValidatorPort).validateToken(token);
  }

  @Test
  void shouldThrowExceptionForInvalidToken() {
    // Given
    String token = "invalid-token";

    when(tokenValidatorPort.validateToken(token))
      .thenThrow(new IllegalArgumentException("Invalid token"));

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> validateTokenService.validateToken(token)
    );

    verify(tokenValidatorPort).validateToken(token);
  }

  @Test
  void shouldReturnTrueForValidToken() {
    // Given
    String token = "valid-jwt-token";

    when(tokenValidatorPort.isTokenValid(token)).thenReturn(true);

    // When
    boolean result = validateTokenService.isTokenValid(token);

    // Then
    assertTrue(result);
    verify(tokenValidatorPort).isTokenValid(token);
  }

  @Test
  void shouldReturnFalseForInvalidToken() {
    // Given
    String token = "invalid-token";

    when(tokenValidatorPort.isTokenValid(token)).thenReturn(false);

    // When
    boolean result = validateTokenService.isTokenValid(token);

    // Then
    assertFalse(result);
    verify(tokenValidatorPort).isTokenValid(token);
  }

  @Test
  void shouldReturnFalseForExpiredToken() {
    // Given
    String token = "expired-token";

    when(tokenValidatorPort.isTokenValid(token)).thenReturn(false);

    // When
    boolean result = validateTokenService.isTokenValid(token);

    // Then
    assertFalse(result);
    verify(tokenValidatorPort).isTokenValid(token);
  }

  @Test
  void shouldHandleNullToken() {
    // Given
    String token = null;

    when(tokenValidatorPort.isTokenValid(token)).thenReturn(false);

    // When
    boolean result = validateTokenService.isTokenValid(token);

    // Then
    assertFalse(result);
    verify(tokenValidatorPort).isTokenValid(token);
  }
}
