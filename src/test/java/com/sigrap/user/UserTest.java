package com.sigrap.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldValidateName() {
    User user = User.builder()
      .name(null)
      .email("test@example.com")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isNotEmpty();
    assertThat(
      violations
        .stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("name"))
    ).isTrue();
  }

  @Test
  void shouldValidateEmail() {
    User user = User.builder()
      .name("Test User")
      .email(null)
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isNotEmpty();
    assertThat(
      violations
        .stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("email"))
    ).isTrue();
  }

  @Test
  void shouldValidateEmailFormat() {
    User user = User.builder()
      .name("Test User")
      .email("invalid-email")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isNotEmpty();
    assertThat(
      violations
        .stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("email"))
    ).isTrue();
  }

  @Test
  void shouldValidatePassword() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password(null)
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isNotEmpty();
    assertThat(
      violations
        .stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("password"))
    ).isTrue();
  }

  @Test
  void shouldValidateValidUser() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldAllowEmptyDocumentId() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .documentId("")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).isEmpty();
    assertThat(
      violations
        .stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("documentId"))
    ).isFalse();
  }
}
