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
 * Contains validated data for recording user clock-ins.
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
 *   <li>User ID must not be null</li>
 *   <li>Timestamp is optional - if not provided, current time in Bogotá will be used</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for clock-in requests")
public class ClockInData {

  /**
   * ID of the user clocking in.
   * Must not be null.
   */
  @NotNull(message = "User ID cannot be null")
  @Schema(description = "ID of the user clocking in", example = "1")
  private Long userId;

  /**
   * Timestamp of the clock-in.
   * If null, current time in Bogotá, Colombia will be used.
   */
  @Schema(
    description = "Timestamp of the clock-in (optional - server time will be used if not provided)",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime timestamp;
}
