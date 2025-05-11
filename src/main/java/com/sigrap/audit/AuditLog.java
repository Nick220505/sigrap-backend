package com.sigrap.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an audit log entry in the system.
 * Records user actions for security and compliance purposes.
 */
@Entity
@Table(name = "audit_logs")
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
   * The ID of the user who performed the action.
   */
  private Long userId;

  /**
   * The username (email) of the user who performed the action.
   * Stored separately in case user records are deleted.
   */
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
  @Column(nullable = false)
  private String entityName;

  /**
   * The ID of the entity that was affected.
   */
  private String entityId;

  /**
   * The previous state of the entity, if applicable.
   * Stored as a JSON string for flexibility.
   */
  @Lob
  @Column(columnDefinition = "TEXT")
  private String oldValue;

  /**
   * The new state of the entity, if applicable.
   * Stored as a JSON string for flexibility.
   */
  @Lob
  @Column(columnDefinition = "TEXT")
  private String newValue;

  /**
   * The timestamp when the action occurred.
   */
  @NotNull
  private LocalDateTime timestamp;

  /**
   * The IP address from which the action was performed.
   * Useful for security auditing.
   */
  private String ipAddress;
}
