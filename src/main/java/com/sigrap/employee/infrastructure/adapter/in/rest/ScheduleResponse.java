package com.sigrap.employee.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for schedule operations.
 * Represents the schedule data returned to REST clients.
 *
 * @param id the schedule identifier
 * @param userId the user identifier
 * @param day the day of the week
 * @param startTime the start time
 * @param endTime the end time
 * @param type the schedule type
 * @param isActive whether the schedule is active
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record ScheduleResponse(
    Long id,
    Long userId,
    String day,
    LocalTime startTime,
    LocalTime endTime,
    String type,
    boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
