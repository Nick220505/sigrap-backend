package com.sigrap.audit;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for audit log information.
 * Contains audit log data returned in API responses.
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
   * The ID of the user who performed the action.
   */
  @Schema(description = "User ID who performed the action", example = "42")
  private Long userId;

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
   * The ID of the entity that was affected.
   */
  @Schema(description = "Entity ID affected", example = "123")
  private String entityId;

  /**
   * The previous state of the entity, if applicable.
   */
  @Schema(
    description = "Previous entity state as JSON",
    example = "{\"name\":\"Old Name\",\"email\":\"old@example.com\"}"
  )
  private String oldValue;

  /**
   * The new state of the entity, if applicable.
   */
  @Schema(
    description = "New entity state as JSON",
    example = "{\"name\":\"New Name\",\"email\":\"new@example.com\"}"
  )
  private String newValue;

  /**
   * The timestamp when the action occurred.
   */
  @Schema(
    description = "Timestamp when the action occurred",
    example = "2023-06-15T10:30:45"
  )
  private LocalDateTime timestamp;

  /**
   * The IP address from which the action was performed.
   */
  @Schema(
    description = "IP address where the action originated",
    example = "192.168.1.1"
  )
  private String ipAddress;
}
