package com.sigrap.employee.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating attendance records.
 * Contains validated attendance data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates attendance input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages attendance information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Required fields must not be null</li>
 *   <li>User ID must reference existing user</li>
 *   <li>Times must be valid</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating an attendance record"
)
public class AttendanceData {

  /**
   * ID of the associated user.
   * Must reference an existing user.
   */
  @NotNull(message = "User ID cannot be null")
  @Schema(description = "ID of the associated user", example = "1")
  private Long userId;

  /**
   * Date of the attendance record.
   * Must not be null.
   */
  @NotNull(message = "Date cannot be null")
  @Schema(
    description = "Date of the attendance record",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime date;

  /**
   * Time when the user clocked in.
   */
  @Schema(description = "Clock-in time", example = "2024-01-15T09:00:00")
  private LocalDateTime clockInTime;

  /**
   * Time when the user clocked out.
   */
  @Schema(description = "Clock-out time", example = "2024-01-15T17:00:00")
  private LocalDateTime clockOutTime;

  /**
   * Status of the attendance record.
   */
  @NotNull(message = "Status cannot be null")
  @Schema(description = "Status of the attendance", example = "PRESENT")
  private AttendanceStatus status;
}
