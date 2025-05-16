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
  void shouldValidateNameNotBlank() {
    User user = User.builder()
      .name("")
      .email("test@example.com")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "name"
    );
  }

  @Test
  void shouldValidateEmailNotBlank() {
    User user = User.builder()
      .name("Test User")
      .email("")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "email"
    );
  }

  @Test
  void shouldValidateEmailFormat() {
    User user = User.builder()
      .name("Test User")
      .email("invalid-email")
      .password("password123")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "email"
    );
  }

  @Test
  void shouldValidatePasswordNotBlank() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "password"
    );
  }

  @Test
  void shouldValidateDocumentIdNotBlank() {
    User user = User.builder()
      .name("Test User")
      .email("test@example.com")
      .password("password123")
      .documentId("")
      .build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "documentId"
    );
  }
}
