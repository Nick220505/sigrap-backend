package com.sigrap.employee.domain.model;

import com.sigrap.user.domain.model.UserId;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain entity representing an employee work schedule.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A schedule defines when an employee is expected to work,
 * including the day of week, start/end times, and schedule type.</p>
 */
public class Schedule {
    private final ScheduleId id;
    private final UserId userId;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String type;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new schedule (without ID).
     * Used when creating a schedule before persistence.
     *
     * @param userId the user identifier (required)
     * @param day the day of week (required)
     * @param startTime the start time (required)
     * @param endTime the end time (required)
     * @param type the schedule type (defaults to "Regular")
     */
    public Schedule(UserId userId, String day, LocalTime startTime, LocalTime endTime, String type) {
        this(null, userId, day, startTime, endTime, type, true,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a schedule from persistence.
     *
     * @param id the schedule identifier
     * @param userId the user identifier (required)
     * @param day the day of week (required)
     * @param startTime the start time (required)
     * @param endTime the end time (required)
     * @param type the schedule type
     * @param isActive whether the schedule is active
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Schedule(ScheduleId id, UserId userId, String day,
                   LocalTime startTime, LocalTime endTime, String type,
                   boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.day = validateDay(day);
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = validateEndTime(startTime, endTime);
        this.type = type != null ? type : "Regular";
        this.isActive = isActive;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Validates the day of week.
     *
     * @param day the day to validate
     * @return the validated day
     * @throws IllegalArgumentException if day is null or blank
     */
    private String validateDay(String day) {
        if (day == null || day.isBlank()) {
            throw new IllegalArgumentException("Day cannot be null or blank");
        }
        return day;
    }

    /**
     * Validates that end time is after start time.
     *
     * @param startTime the start time
     * @param endTime the end time to validate
     * @return the validated end time
     * @throws IllegalArgumentException if end time is null or before start time
     */
    private LocalTime validateEndTime(LocalTime startTime, LocalTime endTime) {
        Objects.requireNonNull(endTime, "End time cannot be null");
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        return endTime;
    }

    /**
     * Updates the schedule day.
     *
     * @param newDay the new day (required)
     */
    public void updateDay(String newDay) {
        String validated = validateDay(newDay);
        if (!this.day.equals(validated)) {
            this.day = validated;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the schedule times.
     *
     * @param newStartTime the new start time (required)
     * @param newEndTime the new end time (required)
     */
    public void updateTimes(LocalTime newStartTime, LocalTime newEndTime) {
        Objects.requireNonNull(newStartTime, "Start time cannot be null");
        LocalTime validatedEndTime = validateEndTime(newStartTime, newEndTime);
        
        if (!this.startTime.equals(newStartTime) || !this.endTime.equals(validatedEndTime)) {
            this.startTime = newStartTime;
            this.endTime = validatedEndTime;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the schedule type.
     *
     * @param newType the new type
     */
    public void updateType(String newType) {
        String type = newType != null ? newType : "Regular";
        if (!this.type.equals(type)) {
            this.type = type;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Activates this schedule.
     */
    public void activate() {
        if (!this.isActive) {
            this.isActive = true;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Deactivates this schedule.
     */
    public void deactivate() {
        if (this.isActive) {
            this.isActive = false;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Calculates the duration of this schedule in hours.
     *
     * @return the duration in hours
     */
    public double getDurationInHours() {
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes / 60.0;
    }

    /**
     * Checks if this schedule is new (not yet persisted).
     *
     * @return true if the schedule has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public ScheduleId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
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
        Schedule schedule = (Schedule) o;
        return Objects.equals(id, schedule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", userId=" + userId +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type='" + type + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
