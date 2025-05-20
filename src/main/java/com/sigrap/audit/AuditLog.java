package com.sigrap.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an audit log entry in the system.
 * Records user actions with rich context for security and compliance purposes.
 */
@Entity
@Table(
  name = "audit_logs",
  indexes = {
    @Index(name = "idx_audit_username", columnList = "username"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_entity_name", columnList = "entity_name"),
    @Index(name = "idx_audit_entity_id", columnList = "entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
  }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

  /**
   * Unique identifier for the audit log entry.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The username (email) of the user who performed the action.
   * Stored separately from user entity in case user records are deleted.
   */
  @NotNull
  @Column(nullable = false)
  private String username;

  /**
   * The action that was performed.
   * Examples: "CREATE", "UPDATE", "DELETE", "LOGIN".
   */
  @NotNull
  @Column(nullable = false)
  private String action;

  /**
   * The name of the entity that was affected.
   * Examples: "User", "Product", "Order".
   */
  @NotNull
  @Column(name = "entity_name", nullable = false)
  private String entityName;

  /**
   * The identifier of the entity that was affected.
   * May be null for operations that don't target a specific entity instance.
   */
  @Column(name = "entity_id")
  private String entityId;

  /**
   * The timestamp when the action occurred.
   */
  @NotNull
  @Column(nullable = false)
  private LocalDateTime timestamp;

  /**
   * The IP address of the client that initiated the action.
   */
  @Column(name = "source_ip")
  private String sourceIp;

  /**
   * The user agent string of the client that initiated the action.
   */
  @Column(name = "user_agent", length = 500)
  private String userAgent;

  /**
   * Additional details about the action, such as before/after values.
   * Stored as JSON string when captureDetails is true.
   */
  @Lob
  @Column(name = "details", columnDefinition = "TEXT")
  private String details;

  /**
   * Status of the action - whether it succeeded or failed.
   * Useful for tracking error rates and failed operations.
   */
  @Builder.Default
  @Column(nullable = false)
  private String status = "SUCCESS";

  /**
   * Duration of the operation in milliseconds.
   * Useful for performance monitoring.
   */
  @Column(name = "duration_ms")
  private Long durationMs;
}
