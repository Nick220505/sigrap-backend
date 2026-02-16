package com.sigrap.employee.domain.model;

import com.sigrap.user.domain.model.UserId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an employee attendance record.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>An attendance record tracks when an employee clocks in and out,
 * calculates total hours worked, and maintains attendance status.</p>
 */
public class Attendance {
    private final AttendanceId id;
    private final UserId userId;
    private final LocalDateTime date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Double totalHours;
    private AttendanceStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new attendance record (without ID).
     * Used when creating an attendance before persistence.
     *
     * @param userId the user identifier (required)
     * @param date the date of attendance (required)
     * @param status the attendance status (required)
     */
    public Attendance(UserId userId, LocalDateTime date, AttendanceStatus status) {
        this(null, userId, date, null, null, null, status, 
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting an attendance from persistence.
     *
     * @param id the attendance identifier
     * @param userId the user identifier (required)
     * @param date the date of attendance (required)
     * @param clockInTime the clock-in time
     * @param clockOutTime the clock-out time
     * @param totalHours the total hours worked
     * @param status the attendance status (required)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Attendance(AttendanceId id, UserId userId, LocalDateTime date,
                     LocalDateTime clockInTime, LocalDateTime clockOutTime,
                     Double totalHours, AttendanceStatus status,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.totalHours = totalHours;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Records the clock-in time for this attendance.
     * Automatically sets status to PRESENT if not already set.
     *
     * @param clockInTime the clock-in time (required)
     * @throws IllegalStateException if already clocked in
     */
    public void clockIn(LocalDateTime clockInTime) {
        Objects.requireNonNull(clockInTime, "Clock-in time cannot be null");
        if (this.clockInTime != null) {
            throw new IllegalStateException("Already clocked in");
        }
        this.clockInTime = clockInTime;
        if (this.status == AttendanceStatus.ABSENT) {
            this.status = AttendanceStatus.PRESENT;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Records the clock-out time for this attendance.
     * Automatically calculates total hours worked.
     *
     * @param clockOutTime the clock-out time (required)
     * @throws IllegalStateException if not clocked in or already clocked out
     * @throws IllegalArgumentException if clock-out time is before clock-in time
     */
    public void clockOut(LocalDateTime clockOutTime) {
        Objects.requireNonNull(clockOutTime, "Clock-out time cannot be null");
        if (this.clockInTime == null) {
            throw new IllegalStateException("Cannot clock out without clocking in first");
        }
        if (this.clockOutTime != null) {
            throw new IllegalStateException("Already clocked out");
        }
        if (clockOutTime.isBefore(this.clockInTime)) {
            throw new IllegalArgumentException("Clock-out time cannot be before clock-in time");
        }
        this.clockOutTime = clockOutTime;
        this.totalHours = calculateTotalHours();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the attendance status.
     *
     * @param newStatus the new status (required)
     */
    public void updateStatus(AttendanceStatus newStatus) {
        Objects.requireNonNull(newStatus, "Status cannot be null");
        if (this.status != newStatus) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Calculates total hours worked based on clock-in and clock-out times.
     *
     * @return the total hours worked, or null if not clocked out
     */
    private Double calculateTotalHours() {
        if (clockInTime == null || clockOutTime == null) {
            return null;
        }
        Duration duration = Duration.between(clockInTime, clockOutTime);
        return duration.toMinutes() / 60.0;
    }

    /**
     * Checks if the employee is currently clocked in.
     *
     * @return true if clocked in but not clocked out, false otherwise
     */
    public boolean isClockedIn() {
        return clockInTime != null && clockOutTime == null;
    }

    /**
     * Checks if the attendance record is complete (both clock-in and clock-out recorded).
     *
     * @return true if both times are recorded, false otherwise
     */
    public boolean isComplete() {
        return clockInTime != null && clockOutTime != null;
    }

    /**
     * Checks if this attendance is new (not yet persisted).
     *
     * @return true if the attendance has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public AttendanceId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", clockInTime=" + clockInTime +
                ", clockOutTime=" + clockOutTime +
                ", totalHours=" + totalHours +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
