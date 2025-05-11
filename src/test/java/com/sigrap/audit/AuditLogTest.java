package com.sigrap.audit;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditLogTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldValidateValidAuditLog() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(LocalDateTime.now())
      .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
      auditLog
    );
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldValidateActionNotNull() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action(null)
      .entityName("User")
      .entityId("1")
      .timestamp(LocalDateTime.now())
      .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
      auditLog
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "action"
    );
  }

  @Test
  void shouldValidateEntityNameNotNull() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName(null)
      .entityId("1")
      .timestamp(LocalDateTime.now())
      .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
      auditLog
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "entityName"
    );
  }

  @Test
  void shouldValidateTimestampNotNull() {
    AuditLog auditLog = AuditLog.builder()
      .userId(1L)
      .username("test@example.com")
      .action("CREATE")
      .entityName("User")
      .entityId("1")
      .timestamp(null)
      .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
      auditLog
    );
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
      "timestamp"
    );
  }
}
