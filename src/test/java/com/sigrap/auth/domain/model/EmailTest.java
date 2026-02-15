package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  void shouldCreateEmailWithValidFormat() {
    Email email = new Email("user@example.com");

    assertEquals("user@example.com", email.value());
  }

  @Test
  void shouldAcceptEmailWithPlusSign() {
    Email email = new Email("user+tag@example.com");

    assertEquals("user+tag@example.com", email.value());
  }

  @Test
  void shouldAcceptEmailWithDots() {
    Email email = new Email("first.last@example.com");

    assertEquals("first.last@example.com", email.value());
  }

  @Test
  void shouldThrowExceptionForNullEmail() {
    assertThrows(NullPointerException.class, () -> new Email(null));
  }

  @Test
  void shouldThrowExceptionForBlankEmail() {
    assertThrows(IllegalArgumentException.class, () -> new Email(""));
    assertThrows(IllegalArgumentException.class, () -> new Email("   "));
  }

  @Test
  void shouldThrowExceptionForInvalidEmailFormat() {
    assertThrows(IllegalArgumentException.class, () -> new Email("invalid"));
    assertThrows(
      IllegalArgumentException.class,
      () -> new Email("invalid@")
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> new Email("@example.com")
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> new Email("invalid@example")
    );
  }

  @Test
  void shouldBeEqualForSameValue() {
    Email email1 = new Email("user@example.com");
    Email email2 = new Email("user@example.com");

    assertEquals(email1, email2);
    assertEquals(email1.hashCode(), email2.hashCode());
  }
}
