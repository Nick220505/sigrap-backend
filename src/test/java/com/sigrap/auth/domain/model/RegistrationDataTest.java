package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegistrationDataTest {

  @Test
  void shouldCreateRegistrationDataWithValidData() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    RegistrationData data = new RegistrationData("John Doe", email, password);

    assertEquals("John Doe", data.name());
    assertEquals(email, data.email());
    assertEquals(password, data.password());
  }

  @Test
  void shouldThrowExceptionForNullName() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    assertThrows(
      NullPointerException.class,
      () -> new RegistrationData(null, email, password)
    );
  }

  @Test
  void shouldThrowExceptionForBlankName() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    assertThrows(
      IllegalArgumentException.class,
      () -> new RegistrationData("", email, password)
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> new RegistrationData("   ", email, password)
    );
  }

  @Test
  void shouldThrowExceptionForNullEmail() {
    Password password = new Password("Password123!");

    assertThrows(
      NullPointerException.class,
      () -> new RegistrationData("John Doe", null, password)
    );
  }

  @Test
  void shouldThrowExceptionForNullPassword() {
    Email email = new Email("user@example.com");

    assertThrows(
      NullPointerException.class,
      () -> new RegistrationData("John Doe", email, null)
    );
  }

  @Test
  void shouldBeEqualForSameValues() {
    Email email = new Email("user@example.com");
    Password password = new Password("Password123!");

    RegistrationData data1 = new RegistrationData("John Doe", email, password);
    RegistrationData data2 = new RegistrationData("John Doe", email, password);

    assertEquals(data1, data2);
    assertEquals(data1.hashCode(), data2.hashCode());
  }
}
