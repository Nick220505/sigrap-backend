package com.sigrap.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing employee performance information.
 * Used for returning performance data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents performance read operations</li>
 *   <li>Includes complete performance details</li>
 *   <li>Contains audit timestamps</li>
 *   <li>Provides performance metrics</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO containing employee performance information")
public class EmployeePerformanceInfo {

  /**
   * Unique identifier of the performance record.
   */
  @Schema(description = "Unique identifier of the performance record")
  private Long id;

  /**
   * ID of the associated employee.
   */
  @Schema(description = "ID of the employee this performance record belongs to")
  private Long employeeId;

  /**
   * Name of the employee.
   */
  @Schema(description = "Full name of the employee")
  private String employeeName;

  /**
   * Start date of the performance period.
   */
  @Schema(description = "Start date of the performance period")
  private LocalDateTime periodStart;

  /**
   * End date of the performance period.
   */
  @Schema(description = "End date of the performance period")
  private LocalDateTime periodEnd;

  /**
   * Number of sales processed during the period.
   */
  @Schema(description = "Number of sales processed during the period")
  private Integer salesCount;

  /**
   * Total amount of sales during the period.
   */
  @Schema(description = "Total amount of sales during the period")
  private BigDecimal salesTotal;

  /**
   * Average transaction amount.
   */
  @Schema(description = "Average transaction amount")
  private BigDecimal transactionAverage;

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

  /**
   * Timestamp of when the performance record was created.
   */
  @Schema(description = "When the record was created")
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the performance record was last updated.
   */
  @Schema(description = "When the record was last updated")
  private LocalDateTime updatedAt;

  /**
   * Average transaction value.
   */
  @Schema(description = "Average transaction value")
  private BigDecimal averageTransactionValue;
}
