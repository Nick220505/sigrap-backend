package com.sigrap.employee.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for clock-in requests.
 * Contains validated data for recording employee clock-ins.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates clock-in input data</li>
 *   <li>Supports clock-in operations</li>
 *   <li>Manages clock-in information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Employee ID must not be null</li>
 *   <li>Timestamp must not be null</li>
 *   <li>Notes are optional</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for clock-in requests")
public class ClockInData {

  /**
   * ID of the employee clocking in.
   * Must not be null.
   */
  @NotNull(message = "Employee ID cannot be null")
  @Schema(description = "ID of the employee clocking in", example = "1")
  private Long employeeId;

  /**
   * Timestamp of the clock-in.
   * Must not be null.
   */
  @NotNull(message = "Timestamp cannot be null")
  @Schema(
    description = "Timestamp of the clock-in",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime timestamp;

  /**
   * Optional notes about the clock-in.
   */
  @Schema(
    description = "Optional notes about the clock-in",
    example = "On time arrival"
  )
  private String notes;
}
