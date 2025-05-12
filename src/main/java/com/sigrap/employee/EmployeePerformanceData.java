package com.sigrap.employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating employee performance records.
 * Contains validated performance data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates performance input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages performance information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Required fields must not be null</li>
 *   <li>Employee ID must reference existing employee</li>
 *   <li>Metrics must be zero or positive</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data object for creating/updating employee performance records"
)
public class EmployeePerformanceData {

  /**
   * ID of the associated employee.
   * Must reference an existing employee.
   */
  @NotNull(message = "Employee ID cannot be null")
  @Schema(description = "ID of the employee this performance record belongs to")
  private Long employeeId;

  /**
   * Start date of the performance period.
   * Must not be null.
   */
  @NotNull(message = "Period start date cannot be null")
  @Schema(description = "Start date of the performance period")
  private LocalDateTime periodStart;

  /**
   * End date of the performance period.
   * Must not be null.
   */
  @NotNull(message = "Period end date cannot be null")
  @Schema(description = "End date of the performance period")
  private LocalDateTime periodEnd;

  /**
   * Number of sales processed during the period.
   * Must be zero or positive.
   */
  @NotNull(message = "Sales count cannot be null")
  @PositiveOrZero(message = "Sales count must be zero or positive")
  @Schema(description = "Number of sales processed during the period")
  private Integer salesCount;

  /**
   * Total amount of sales during the period.
   * Must be zero or positive.
   */
  @NotNull(message = "Sales total cannot be null")
  @PositiveOrZero(message = "Sales total must be zero or positive")
  @Schema(description = "Total amount of sales during the period")
  private BigDecimal salesTotal;

  /**
   * Performance rating (0-100).
   */
  @Schema(description = "Performance rating (0-100)")
  private Integer rating;

  /**
   * Additional notes about the performance.
   */
  @Schema(description = "Additional notes about the performance")
  private String notes;
}
