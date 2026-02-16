package com.sigrap.employee.domain.model;

/**
 * Value object representing an attendance identifier.
 * Immutable and self-validating.
 */
public record AttendanceId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the attendance ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public AttendanceId {
        if (value == null) {
            throw new IllegalArgumentException("Attendance ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Attendance ID must be positive");
        }
    }
}
