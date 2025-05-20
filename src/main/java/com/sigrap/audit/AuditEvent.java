package com.sigrap.audit;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Builder;
import lombok.Data;

/**
 * Event object representing an action that should be audited.
 * Used for decoupling audit logging from business logic through event publishing.
 */
@Data
@Builder
public class AuditEvent {

  /**
   * The username (email) of the user who performed the action.
   */
  private String username;

  /**
   * The action that was performed.
   * Examples: "CREATE", "UPDATE", "DELETE", "LOGIN".
   */
  private String action;

  /**
   * The name of the entity that was affected.
   * Examples: "User", "Product", "Order".
   */
  private String entityName;

  /**
   * The identifier of the entity that was affected.
   */
  private String entityId;

  /**
   * The timestamp when the action occurred.
   * Uses Colombia/Bogota timezone (UTC-5).
   */
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now(
    ZoneId.of("America/Bogota")
  );

  /**
   * The IP address of the client that initiated the action.
   */
  private String sourceIp;

  /**
   * The user agent string of the client that initiated the action.
   */
  private String userAgent;

  /**
   * Additional details about the action, such as before/after values.
   */
  private String details;

  /**
   * Status of the action - whether it succeeded or failed.
   */
  @Builder.Default
  private String status = "SUCCESS";

  /**
   * Duration of the operation in milliseconds.
   */
  private Long durationMs;
}
