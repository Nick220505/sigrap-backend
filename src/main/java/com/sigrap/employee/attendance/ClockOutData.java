package com.sigrap.employee.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for clock-out requests.
 * Contains validated data for recording employee clock-outs.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates clock-out input data</li>
 *   <li>Supports clock-out operations</li>
 *   <li>Manages clock-out information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Attendance ID must not be null</li>
 *   <li>Timestamp is optional - if not provided, current time in Bogotá will be used</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for clock-out requests")
public class ClockOutData {

  /**
   * ID of the attendance record to update.
   * Must not be null.
   */
  @NotNull(message = "Attendance ID cannot be null")
  @Schema(description = "ID of the attendance record", example = "1")
  private Long attendanceId;

  /**
   * Timestamp of the clock-out.
   * If null, current time in Bogotá, Colombia will be used.
   */
  @Schema(
    description = "Timestamp of the clock-out (optional - server time will be used if not provided)",
    example = "2024-01-15T17:00:00"
  )
  private LocalDateTime timestamp;
}
