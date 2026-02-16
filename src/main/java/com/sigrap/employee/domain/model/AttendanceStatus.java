package com.sigrap.employee.domain.model;

/**
 * Enum representing possible employee attendance statuses.
 * Part of the domain model with no framework dependencies.
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
    ON_LEAVE
}
