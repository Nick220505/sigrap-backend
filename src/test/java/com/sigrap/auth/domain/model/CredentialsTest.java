package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CredentialsTest {

  @Test
  void shouldCreateCredentialsWithValidData() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    Credentials credentials = new Credentials(email, password);

    assertEquals(email, credentials.email());
    assertEquals(password, credentials.password());
  }

  @Test
  void shouldThrowExceptionForNullEmail() {
    Password password = new Password("Password123!");

    assertThrows(
      NullPointerException.class,
      () -> new Credentials(null, password)
    );
  }

  @Test
  void shouldThrowExceptionForNullPassword() {
    Email email = new Email("user@example.com");

    assertThrows(
      NullPointerException.class,
      () -> new Credentials(email, null)
    );
  }

  @Test
  void shouldBeEqualForSameValues() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    Credentials credentials1 = new Credentials(email, password);
    Credentials credentials2 = new Credentials(email, password);

    assertEquals(credentials1, credentials2);
    assertEquals(credentials1.hashCode(), credentials2.hashCode());
  }
}
