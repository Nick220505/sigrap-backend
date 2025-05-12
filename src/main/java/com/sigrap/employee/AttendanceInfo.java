package com.sigrap.employee;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing attendance information.
 * Used for returning attendance data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents attendance read operations</li>
 *   <li>Includes complete attendance details</li>
 *   <li>Contains audit timestamps</li>
 *   <li>Provides status information</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing attendance information")
public class AttendanceInfo {

  /**
   * Unique identifier of the attendance record.
   */
  @Schema(
    description = "Unique identifier of the attendance record",
    example = "1"
  )
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
   * Date of the attendance record.
   */
  @Schema(
    description = "Date of the attendance record",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime date;

  /**
   * Time when the employee clocked in.
   */
  @Schema(description = "Clock-in time", example = "2024-01-15T09:00:00")
  private LocalDateTime clockInTime;

  /**
   * Time when the employee clocked out.
   */
  @Schema(description = "Clock-out time", example = "2024-01-15T17:00:00")
  private LocalDateTime clockOutTime;

  /**
   * Total hours worked in this attendance period.
   */
  @Schema(description = "Total hours worked", example = "8.0")
  private Double totalHours;

  /**
   * Status of the attendance record.
   */
  @Schema(description = "Status of the attendance", example = "PRESENT")
  private Attendance.AttendanceStatus status;

  /**
   * Additional notes or comments about the attendance.
   */
  @Schema(
    description = "Additional notes about the attendance",
    example = "Employee arrived on time"
  )
  private String notes;

  /**
   * Timestamp of when the attendance record was created.
   */
  @Schema(
    description = "Creation timestamp of the attendance record",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the attendance record was last updated.
   */
  @Schema(
    description = "Last update timestamp of the attendance record",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime updatedAt;
}
