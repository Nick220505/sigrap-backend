package com.sigrap.employee.schedule;

import com.sigrap.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing user work schedules.
 * Tracks user work hours and shift assignments.
 *
 * <p>This entity maintains schedule information including:
 * <ul>
 *   <li>Work days and times</li>
 *   <li>Shift assignments</li>
 *   <li>Schedule status</li>
 *   <li>Audit information</li>
 * </ul></p>
 */
@Entity
@Table(name = "schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

  /**
   * Unique identifier for the schedule record.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * User associated with this schedule.
   * Many-to-one relationship with User.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "User reference cannot be null")
  private User user;

  /**
   * Day of the week for this schedule.
   * Must not be null.
   */
  @NotNull(message = "Day cannot be null")
  @Column(name = "week_day", nullable = false)
  private String day;

  /**
   * Start time of the work shift.
   * Must not be null.
   */
  @NotNull(message = "Start time cannot be null")
  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  /**
   * End time of the work shift.
   * Must not be null.
   */
  @NotNull(message = "End time cannot be null")
  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  /**
   * Whether this schedule is currently active.
   */
  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  /**
   * Timestamp of when the schedule was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the schedule was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
