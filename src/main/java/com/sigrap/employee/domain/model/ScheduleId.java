package com.sigrap.employee.domain.model;

/**
 * Value object representing a schedule identifier.
 * Immutable and self-validating.
 */
public record ScheduleId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the schedule ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public ScheduleId {
        if (value == null) {
            throw new IllegalArgumentException("Schedule ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Schedule ID must be positive");
        }
    }
}
