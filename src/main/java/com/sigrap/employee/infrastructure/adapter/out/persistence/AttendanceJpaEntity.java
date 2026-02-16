package com.sigrap.employee.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for persisting attendance data.
 * This is a pure persistence model with no business logic.
 * Maps to the "attendances" table in the database.
 */
@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceJpaEntity {

    /**
     * Unique identifier for the attendance record.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User identifier for the employee.
     * References the user who owns this attendance record.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Date of the attendance record.
     * Represents when the attendance occurred.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Time when the employee clocked in.
     * Null if not yet clocked in.
     */
    @Column(name = "clock_in_time")
    private LocalDateTime clockInTime;

    /**
     * Time when the employee clocked out.
     * Null if not yet clocked out.
     */
    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    /**
     * Total hours worked.
     * Calculated based on clock-in and clock-out times.
     */
    @Column(name = "total_hours")
    private Double totalHours;

    /**
     * Attendance status (PRESENT, ABSENT, LATE, etc.).
     * Stored as string in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatusJpa status;

    /**
     * Timestamp of when the attendance record was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the attendance record was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA-specific enum for attendance status.
     * Mirrors the domain AttendanceStatus enum.
     */
    public enum AttendanceStatusJpa {
        PRESENT,
        ABSENT,
        LATE,
        EARLY_DEPARTURE,
        ON_LEAVE
    }
}
