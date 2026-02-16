package com.sigrap.employee.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for persisting schedule data.
 * This is a pure persistence model with no business logic.
 * Maps to the "schedules" table in the database.
 */
@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleJpaEntity {

    /**
     * Unique identifier for the schedule.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User identifier for the employee.
     * References the user who owns this schedule.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Day of the week for this schedule.
     * Examples: "Monday", "Tuesday", etc.
     */
    @Column(name = "day_of_week", nullable = false, length = 20)
    private String day;

    /**
     * Start time of the work shift.
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * End time of the work shift.
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Type of schedule (e.g., "Regular", "Overtime", "Part-time").
     */
    @Column(length = 50)
    private String type;

    /**
     * Whether this schedule is currently active.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * Timestamp of when the schedule was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the schedule was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
