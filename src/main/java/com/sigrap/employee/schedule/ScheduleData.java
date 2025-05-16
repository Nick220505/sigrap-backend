package com.sigrap.employee.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating schedules.
 * Contains validated schedule data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates schedule input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages schedule information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Required fields must not be blank</li>
 *   <li>Times must be valid</li>
 *   <li>User ID must reference existing user</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating a schedule"
)
public class ScheduleData {

  /**
   * ID of the associated user.
   * Must reference an existing user.
   */
  @NotNull(message = "User ID cannot be null")
  @Schema(description = "ID of the associated user", example = "1")
  private Long userId;

  /**
   * Day of the week for this schedule.
   * Must not be blank.
   */
  @NotBlank(message = "Day cannot be blank")
  @Schema(description = "Day of the week", example = "MONDAY")
  private String day;

  /**
   * Start time of the work shift.
   * Must not be null.
   */
  @NotNull(message = "Start time cannot be null")
  @Schema(description = "Start time of the shift", example = "09:00")
  private LocalTime startTime;

  /**
   * End time of the work shift.
   * Must not be null.
   */
  @NotNull(message = "End time cannot be null")
  @Schema(description = "End time of the shift", example = "17:00")
  private LocalTime endTime;

  /**
   * Whether this schedule is active.
   */
  @Schema(description = "Whether the schedule is active", example = "true")
  private Boolean isActive;
}
