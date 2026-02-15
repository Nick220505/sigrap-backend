package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuthenticationResultTest {

  @Test
  void shouldCreateAuthenticationResultWithValidData() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    Email email = new Email("user@example.com");
    LocalDateTime lastLogin = LocalDateTime.now();

    AuthenticationResult result = new AuthenticationResult(
      token,
      email,
      "John Doe",
      lastLogin,
      "ADMIN"
    );

    assertEquals(token, result.getToken());
    assertEquals(email, result.getEmail());
    assertEquals("John Doe", result.getName());
    assertEquals(lastLogin, result.getLastLogin());
    assertEquals("ADMIN", result.getRole());
  }

  @Test
  void shouldThrowExceptionForNullToken() {
    Email email = new Email("user@example.com");
    LocalDateTime lastLogin = LocalDateTime.now();

    assertThrows(
      NullPointerException.class,
      () ->
        new AuthenticationResult(null, email, "John Doe", lastLogin, "ADMIN")
    );
  }

  @Test
  void shouldThrowExceptionForNullEmail() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    LocalDateTime lastLogin = LocalDateTime.now();

    assertThrows(
      NullPointerException.class,
      () ->
        new AuthenticationResult(token, null, "John Doe", lastLogin, "ADMIN")
    );
  }

  @Test
  void shouldThrowExceptionForNullName() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    Email email = new Email("user@example.com");
    LocalDateTime lastLogin = LocalDateTime.now();

    assertThrows(
      NullPointerException.class,
      () -> new AuthenticationResult(token, email, null, lastLogin, "ADMIN")
    );
  }

  @Test
  void shouldThrowExceptionForNullLastLogin() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    Email email = new Email("user@example.com");

    assertThrows(
      NullPointerException.class,
      () -> new AuthenticationResult(token, email, "John Doe", null, "ADMIN")
    );
  }

  @Test
  void shouldThrowExceptionForNullRole() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    Email email = new Email("user@example.com");
    LocalDateTime lastLogin = LocalDateTime.now();

    assertThrows(
      NullPointerException.class,
      () ->
        new AuthenticationResult(token, email, "John Doe", lastLogin, null)
    );
  }

  @Test
  void shouldBeEqualForSameValues() {
    JwtToken token = new JwtToken(
      "token-value",
      LocalDateTime.now().plusHours(1)
    );
    Email email = new Email("user@example.com");
    LocalDateTime lastLogin = LocalDateTime.now();

    AuthenticationResult result1 = new AuthenticationResult(
      token,
      email,
      "John Doe",
      lastLogin,
      "ADMIN"
    );
    AuthenticationResult result2 = new AuthenticationResult(
      token,
      email,
      "John Doe",
      lastLogin,
      "ADMIN"
    );

    assertEquals(result1, result2);
    assertEquals(result1.hashCode(), result2.hashCode());
  }
}
