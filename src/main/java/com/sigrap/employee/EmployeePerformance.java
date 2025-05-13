package com.sigrap.employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing employee performance metrics.
 * Tracks employee performance data including sales and customer satisfaction.
 *
 * <p>This entity maintains performance information including:
 * <ul>
 *   <li>Sales metrics</li>
 *   <li>Transaction counts</li>
 *   <li>Customer satisfaction</li>
 *   <li>Audit information</li>
 * </ul></p>
 */
@Entity
@Table(name = "employee_performance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePerformance {

  /**
   * Unique identifier for the performance record.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Employee associated with this performance record.
   * Many-to-one relationship with Employee.
   */
  @ManyToOne
  @JoinColumn(name = "employee_id", nullable = false)
  @NotNull(message = "Employee reference cannot be null")
  private Employee employee;

  /**
   * Period start date of the performance record.
   * Must not be null.
   */
  @NotNull(message = "Period start date cannot be null")
  @Column(name = "period_start", nullable = false)
  private LocalDateTime periodStart;

  /**
   * Period end date of the performance record.
   * Must not be null.
   */
  @NotNull(message = "Period end date cannot be null")
  @Column(name = "period_end", nullable = false)
  private LocalDateTime periodEnd;

  /**
   * Number of sales processed.
   * Must be zero or positive.
   */
  @PositiveOrZero(message = "Sales count must be zero or positive")
  @Column(name = "sales_count", nullable = false)
  private Integer salesCount;

  /**
   * Total sales amount processed.
   * Must be zero or positive.
   */
  @PositiveOrZero(message = "Sales total must be zero or positive")
  @Column(name = "sales_total", nullable = false)
  private BigDecimal salesTotal;

  /**
   * Average transaction amount processed.
   * Must be zero or positive.
   */
  @Column(name = "transaction_average", nullable = false)
  private BigDecimal transactionAverage;

  /**
   * Customer satisfaction rating.
   * Must be zero or positive.
   */
  @Column(name = "rating", nullable = false)
  @Builder.Default
  private Integer rating = 0;

  /**
   * Additional notes about the performance.
   */
  @Column(name = "notes")
  private String notes;

  /**
   * Timestamp of when the performance record was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the performance record was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  @PreUpdate
  private void calculateTransactionAverage() {
    if (salesCount != null && salesCount > 0 && salesTotal != null) {
      this.transactionAverage = salesTotal.divide(
        BigDecimal.valueOf(salesCount),
        2,
        RoundingMode.HALF_UP
      );
    } else {
      this.transactionAverage = BigDecimal.ZERO;
    }
  }
}
