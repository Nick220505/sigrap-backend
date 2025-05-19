package com.sigrap.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

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
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
        auditLog);
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldValidateActionNotNull() {
    AuditLog auditLog = AuditLog.builder()
        .username("test@example.com")
        .action(null)
        .entityName("User")
        .timestamp(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
        auditLog);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
        "action");
  }

  @Test
  void shouldValidateEntityNameNotNull() {
    AuditLog auditLog = AuditLog.builder()
        .username("test@example.com")
        .action("CREATE")
        .entityName(null)
        .timestamp(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
        auditLog);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
        "entityName");
  }

  @Test
  void shouldValidateTimestampNotNull() {
    AuditLog auditLog = AuditLog.builder()
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(null)
        .build();

    Set<ConstraintViolation<AuditLog>> violations = validator.validate(
        auditLog);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath()).hasToString(
        "timestamp");
  }

  @Test
  void testAuditLogBuilder() {
    LocalDateTime now = LocalDateTime.now();
    AuditLog log = AuditLog.builder()
        .id(1L)
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(now)
        .build();

    assertEquals(1L, log.getId());
    assertEquals("test@example.com", log.getUsername());
    assertEquals("CREATE", log.getAction());
    assertEquals("User", log.getEntityName());
    assertEquals(now, log.getTimestamp());
  }

  @Test
  void testAuditLogEquals() {
    LocalDateTime timestamp = LocalDateTime.now();
    AuditLog log1 = AuditLog.builder()
        .id(1L)
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(timestamp)
        .build();

    AuditLog log2 = AuditLog.builder()
        .id(1L)
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(timestamp)
        .build();

    assertEquals(log1, log2);
    assertEquals(log1.hashCode(), log2.hashCode());
  }

  @Test
  void testAuditLogToString() {
    LocalDateTime timestamp = LocalDateTime.of(2025, 5, 1, 10, 0);
    AuditLog log = AuditLog.builder()
        .id(1L)
        .username("test@example.com")
        .action("CREATE")
        .entityName("User")
        .timestamp(timestamp)
        .build();

    String expectedString = "AuditLog(id=1, username=test@example.com, action=CREATE, entityName=User, timestamp=2025-05-01T10:00)";
    assertEquals(expectedString, log.toString());
  }
}
