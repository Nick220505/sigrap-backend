package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  void shouldCreatePasswordWithValidFormat() {
    Password password = new Password("Password123!");

    assertEquals("Password123!", password.value());
  }

  @Test
  void shouldAcceptPasswordWithAllRequiredCharacters() {
    Password password = new Password("Abcdef1@");

    assertEquals("Abcdef1@", password.value());
  }

  @Test
  void shouldThrowExceptionForNullPassword() {
    assertThrows(NullPointerException.class, () -> new Password(null));
  }

  @Test
  void shouldThrowExceptionForBlankPassword() {
    assertThrows(IllegalArgumentException.class, () -> new Password(""));
    assertThrows(IllegalArgumentException.class, () -> new Password("   "));
  }

  @Test
  void shouldThrowExceptionForPasswordTooShort() {
    assertThrows(IllegalArgumentException.class, () -> new Password("Pass1!"));
  }

  @Test
  void shouldThrowExceptionForPasswordWithoutUppercase() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Password("password123!")
    );
  }

  @Test
  void shouldThrowExceptionForPasswordWithoutLowercase() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Password("PASSWORD123!")
    );
  }

  @Test
  void shouldThrowExceptionForPasswordWithoutNumber() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Password("Password!")
    );
  }

  @Test
  void shouldThrowExceptionForPasswordWithoutSpecialCharacter() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new Password("Password123")
    );
  }

  @Test
  void shouldBeEqualForSameValue() {
    Password password1 = new Password("Password123!");
    Password password2 = new Password("Password123!");

    assertEquals(password1, password2);
    assertEquals(password1.hashCode(), password2.hashCode());
  }
}
