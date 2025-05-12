package com.sigrap.employee;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing activity log information.
 * Used for returning activity log data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Provides complete activity details</li>
 *   <li>Includes employee information</li>
 *   <li>Contains audit timestamps</li>
 *   <li>Supports OpenAPI documentation</li>
 * </ul></p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Full activity context</li>
 *   <li>Employee identification</li>
 *   <li>Temporal tracking</li>
 *   <li>Security information</li>
 * </ul></p>
 *
 * <p>Usage example:
 * <pre>
 * ActivityLogInfo info = ActivityLogInfo.builder()
 *     .id(1L)
 *     .employeeId(1L)
 *     .employeeName("John Doe")
 *     .timestamp(LocalDateTime.now())
 *     .actionType(ActionType.CREATE)
 *     .description("Created new product")
 *     .moduleName("inventory")
 *     .ipAddress("192.168.1.1")
 *     .build();
 * </pre></p>
 *
 * @see ActivityLog
 * @see ActivityLogMapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO containing activity log information")
public class ActivityLogInfo {

  /**
   * Unique identifier of the activity log entry.
   */
  @Schema(description = "Unique identifier of the activity log")
  private Long id;

  /**
   * ID of the employee who performed the action.
   */
  @Schema(description = "ID of the employee who performed the action")
  private Long employeeId;

  /**
   * Full name of the employee for display purposes.
   */
  @Schema(description = "Full name of the employee")
  private String employeeName;

  /**
   * When the action occurred.
   */
  @Schema(description = "When the action occurred")
  private LocalDateTime timestamp;

  /**
   * Type of action that was performed.
   */
  @Schema(description = "Type of action performed")
  private ActivityLog.ActionType actionType;

  /**
   * Detailed description of the action.
   */
  @Schema(description = "Description of the action")
  private String description;

  /**
   * System module where the action occurred.
   */
  @Schema(description = "Name of the module where the action occurred")
  private String moduleName;

  /**
   * ID of the entity affected by the action.
   */
  @Schema(description = "ID of the entity affected by the action")
  private String entityId;

  /**
   * IP address from which the action was performed.
   */
  @Schema(description = "IP address of the client")
  private String ipAddress;

  /**
   * When the log entry was created in the system.
   */
  @Schema(description = "When the log was created")
  private LocalDateTime createdAt;
}
