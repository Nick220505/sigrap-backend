package com.sigrap.employee;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing employee attendance records.
 * Tracks employee clock-in/clock-out times and attendance status.
 *
 * <p>This entity maintains attendance information including:
 * <ul>
 *   <li>Clock-in and clock-out times</li>
 *   <li>Total hours worked</li>
 *   <li>Attendance status (present, absent, late)</li>
 *   <li>Notes or comments about attendance</li>
 * </ul></p>
 */
@Entity
@Table(name = "attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

  /**
   * Unique identifier for the attendance record.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Employee associated with this attendance record.
   * Many-to-one relationship with Employee.
   */
  @ManyToOne
  @JoinColumn(name = "employee_id", nullable = false)
  @NotNull(message = "Employee reference cannot be null")
  private Employee employee;

  /**
   * Date of the attendance record.
   * Must not be null.
   */
  @NotNull(message = "Date cannot be null")
  private LocalDateTime date;

  /**
   * Time when the employee clocked in.
   */
  @Column(name = "clock_in_time")
  private LocalDateTime clockInTime;

  /**
   * Time when the employee clocked out.
   */
  @Column(name = "clock_out_time")
  private LocalDateTime clockOutTime;

  /**
   * Total hours worked in this attendance period.
   * Calculated from clock-in and clock-out times.
   */
  @Column(name = "total_hours")
  private Double totalHours;

  /**
   * Status of the attendance record.
   */
  @Enumerated(EnumType.STRING)
  @NotNull(message = "Status cannot be null")
  @Builder.Default
  private AttendanceStatus status = AttendanceStatus.PRESENT;

  /**
   * Additional notes or comments about the attendance.
   */
  @Column(columnDefinition = "TEXT")
  private String notes;

  /**
   * Timestamp of when the attendance record was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the attendance record was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Enum representing possible attendance statuses.
   */
  public enum AttendanceStatus {
    /**
     * Employee was present for their shift.
     */
    PRESENT,

    /**
     * Employee was absent from their shift.
     */
    ABSENT,

    /**
     * Employee arrived late for their shift.
     */
    LATE,

    /**
     * Employee left early from their shift.
     */
    EARLY_DEPARTURE,

    /**
     * Employee was on approved leave.
     */
    ON_LEAVE,
  }
}
