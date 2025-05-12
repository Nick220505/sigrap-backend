package com.sigrap.employee;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating activity log entries.
 * Contains validated data required to log a new activity.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates required fields</li>
 *   <li>Provides a clean API contract</li>
 *   <li>Supports activity logging operations</li>
 *   <li>Includes OpenAPI documentation</li>
 * </ul></p>
 *
 * <p>Validation rules:
 * <ul>
 *   <li>Employee ID must be provided</li>
 *   <li>Timestamp must be provided</li>
 *   <li>Action type must be specified</li>
 *   <li>Description must be provided</li>
 *   <li>Module name must be specified</li>
 *   <li>IP address must be provided</li>
 * </ul></p>
 *
 * <p>Usage example:
 * <pre>
 * ActivityLogData logData = ActivityLogData.builder()
 *     .employeeId(1L)
 *     .timestamp(LocalDateTime.now())
 *     .actionType(ActionType.CREATE)
 *     .description("Created new product")
 *     .moduleName("inventory")
 *     .ipAddress("192.168.1.1")
 *     .build();
 * </pre></p>
 *
 * @see ActivityLog
 * @see ActivityLogService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data object for creating activity logs")
public class ActivityLogData {

  /**
   * ID of the employee performing the action.
   * Required to establish action ownership.
   */
  @NotNull(message = "Employee ID cannot be null")
  @Schema(description = "ID of the employee performing the action")
  private Long employeeId;

  /**
   * When the action occurred.
   * Required for temporal tracking.
   */
  @NotNull(message = "Timestamp cannot be null")
  @Schema(description = "When the action occurred")
  private LocalDateTime timestamp;

  /**
   * Type of action performed.
   * Required to categorize the activity.
   */
  @NotNull(message = "Action type cannot be null")
  @Schema(description = "Type of action performed")
  private ActivityLog.ActionType actionType;

  /**
   * Description of what was done.
   * Required to provide context.
   */
  @NotNull(message = "Description cannot be null")
  @Schema(description = "Description of the action")
  private String description;

  /**
   * System module where the action occurred.
   * Required for organizational purposes.
   */
  @NotNull(message = "Module name cannot be null")
  @Schema(description = "Name of the module where the action occurred")
  private String moduleName;

  /**
   * ID of the affected entity.
   * Optional reference to specific data.
   */
  @Schema(description = "ID of the entity affected by the action")
  private String entityId;

  /**
   * IP address of the client.
   * Required for security tracking.
   */
  @NotNull(message = "IP address cannot be null")
  @Schema(description = "IP address of the client")
  private String ipAddress;
}
