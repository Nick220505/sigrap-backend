package com.sigrap.audit;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for audit log information.
 * Contains comprehensive audit log data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audit log information")
public class AuditLogInfo {

  /**
   * Unique identifier for the audit log entry.
   */
  @Schema(description = "Audit log ID", example = "1")
  private Long id;

  /**
   * The username (email) of the user who performed the action.
   */
  @Schema(
    description = "Username who performed the action",
    example = "john.doe@example.com"
  )
  private String username;

  /**
   * The action that was performed.
   */
  @Schema(description = "Action performed", example = "UPDATE")
  private String action;

  /**
   * The name of the entity that was affected.
   */
  @Schema(description = "Entity type affected", example = "User")
  private String entityName;

  /**
   * The identifier of the entity that was affected.
   */
  @Schema(description = "Identifier of the entity affected", example = "123")
  private String entityId;

  /**
   * The timestamp when the action occurred.
   */
  @Schema(
    description = "Timestamp when the action occurred",
    example = "2023-06-15T10:30:45"
  )
  private LocalDateTime timestamp;

  /**
   * The IP address of the client that initiated the action.
   */
  @Schema(description = "Source IP address", example = "192.168.1.1")
  private String sourceIp;

  /**
   * The user agent string of the client that initiated the action.
   */
  @Schema(
    description = "User agent string",
    example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)..."
  )
  private String userAgent;

  /**
   * Additional details about the action, such as before/after values.
   */
  @Schema(
    description = "Additional details about the action",
    example = "{\"before\": {...}, \"after\": {...}}"
  )
  private String details;

  /**
   * Status of the action - whether it succeeded or failed.
   */
  @Schema(description = "Status of the action", example = "SUCCESS")
  private String status;

  /**
   * Duration of the operation in milliseconds.
   */
  @Schema(
    description = "Duration of the operation in milliseconds",
    example = "150"
  )
  private Long durationMs;

  /**
   * The old value/state of the entity before the action.
   */
  @Schema(
    description = "Old value/state before the change",
    example = "{\"id\":1,\"name\":\"Old Name\"}"
  )
  private Object oldValue;

  /**
   * The new value/state of the entity after the action.
   */
  @Schema(
    description = "New value/state after the change",
    example = "{\"id\":1,\"name\":\"New Name\"}"
  )
  private Object newValue;
}
