package com.sigrap.role;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldValidateValidRole() {
    Role role = Role.builder()
      .name("ADMIN")
      .description("Administrator role with full permissions")
      .build();

    Set<ConstraintViolation<Role>> violations = validator.validate(role);
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldValidateNameNotBlank() {
    Role role = Role.builder()
      .name("")
      .description("Administrator role with full permissions")
      .build();

    Set<ConstraintViolation<Role>> violations = validator.validate(role);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "name"
    );
  }

  @Test
  void shouldAcceptNullDescription() {
    Role role = Role.builder().name("ADMIN").description(null).build();

    Set<ConstraintViolation<Role>> violations = validator.validate(role);
    assertThat(violations).isEmpty();
  }
}
