package com.sigrap.employee.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

/**
 * Request DTO for schedule creation operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param userId the user identifier (required)
 * @param day the day of the week (required, max 20 characters)
 * @param startTime the start time (required)
 * @param endTime the end time (required)
 * @param type the schedule type (optional, max 50 characters)
 */
public record ScheduleRequest(
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotBlank(message = "Day is required")
    @Size(max = 20, message = "Day cannot exceed 20 characters")
    String day,
    
    @NotNull(message = "Start time is required")
    LocalTime startTime,
    
    @NotNull(message = "End time is required")
    LocalTime endTime,
    
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    String type
) {}
