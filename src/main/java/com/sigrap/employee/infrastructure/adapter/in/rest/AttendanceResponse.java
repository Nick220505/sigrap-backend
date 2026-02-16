package com.sigrap.employee.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

/**
 * Response DTO for attendance operations.
 * Represents the attendance data returned to REST clients.
 *
 * @param id the attendance identifier
 * @param userId the user identifier
 * @param date the date of attendance
 * @param clockInTime the clock-in time
 * @param clockOutTime the clock-out time
 * @param totalHours the total hours worked
 * @param status the attendance status
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record AttendanceResponse(
    Long id,
    Long userId,
    LocalDateTime date,
    LocalDateTime clockInTime,
    LocalDateTime clockOutTime,
    Double totalHours,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
