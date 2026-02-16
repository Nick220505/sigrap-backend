package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Employee schedule response with all details")
public record ScheduleResponse(
    @Schema(description = "Schedule unique identifier", example = "1")
    Long id,
    
    @Schema(description = "User unique identifier", example = "1")
    Long userId,
    
    @Schema(description = "Day of the week", example = "MONDAY")
    String day,
    
    @Schema(description = "Schedule start time", example = "08:00:00")
    LocalTime startTime,
    
    @Schema(description = "Schedule end time", example = "17:00:00")
    LocalTime endTime,
    
    @Schema(description = "Schedule type", example = "REGULAR")
    String type,
    
    @Schema(description = "Whether the schedule is active", example = "true")
    boolean isActive,
    
    @Schema(description = "Creation timestamp", example = "2026-02-15T10:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp", example = "2026-02-15T10:00:00")
    LocalDateTime updatedAt
) {}
