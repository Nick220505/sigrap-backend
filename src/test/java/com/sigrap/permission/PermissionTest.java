package com.sigrap.permission;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldValidateValidPermission() {
    Permission permission = Permission.builder()
      .name("USER_CREATE")
      .resource("USER")
      .action("CREATE")
      .build();

    Set<ConstraintViolation<Permission>> violations = validator.validate(
      permission
    );
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldValidateNameNotBlank() {
    Permission permission = Permission.builder()
      .name("")
      .resource("USER")
      .action("CREATE")
      .build();

    Set<ConstraintViolation<Permission>> violations = validator.validate(
      permission
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "name"
    );
  }

  @Test
  void shouldValidateResourceNotBlank() {
    Permission permission = Permission.builder()
      .name("USER_CREATE")
      .resource("")
      .action("CREATE")
      .build();

    Set<ConstraintViolation<Permission>> violations = validator.validate(
      permission
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "resource"
    );
  }

  @Test
  void shouldValidateActionNotBlank() {
    Permission permission = Permission.builder()
      .name("USER_CREATE")
      .resource("USER")
      .action("")
      .build();

    Set<ConstraintViolation<Permission>> violations = validator.validate(
      permission
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "action"
    );
  }
}
