package com.sigrap.employee.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing schedule information.
 * Used for returning schedule data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents schedule read operations</li>
 *   <li>Includes complete schedule details</li>
 *   <li>Contains audit timestamps</li>
 *   <li>Provides status information</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing schedule information")
public class ScheduleInfo {

  /**
   * Unique identifier of the schedule.
   */
  @Schema(description = "Unique identifier of the schedule", example = "1")
  private Long id;

  /**
   * ID of the associated employee.
   */
  @Schema(description = "ID of the associated employee", example = "1")
  private Long employeeId;

  /**
   * Name of the employee.
   */
  @Schema(description = "Name of the employee", example = "John Doe")
  private String employeeName;

  /**
   * Day of the week for this schedule.
   */
  @Schema(description = "Day of the week", example = "MONDAY")
  private String day;

  /**
   * Start time of the work shift.
   */
  @Schema(description = "Start time of the shift", example = "09:00")
  private LocalTime startTime;

  /**
   * End time of the work shift.
   */
  @Schema(description = "End time of the shift", example = "17:00")
  private LocalTime endTime;

  /**
   * Whether this schedule is active.
   */
  @Schema(description = "Whether the schedule is active", example = "true")
  private Boolean isActive;

  /**
   * Timestamp of when the schedule was created.
   */
  @Schema(
    description = "Creation timestamp of the schedule",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the schedule was last updated.
   */
  @Schema(
    description = "Last update timestamp of the schedule",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime updatedAt;
}
