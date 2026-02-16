package com.sigrap.employee.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Size;
import java.time.LocalTime;

/**
 * Request DTO for schedule update operations.
 * All fields are optional - only provided fields will be updated.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param day the day of the week (optional, max 20 characters)
 * @param startTime the start time (optional)
 * @param endTime the end time (optional)
 * @param type the schedule type (optional, max 50 characters)
 * @param isActive whether the schedule is active (optional)
 */
public record UpdateScheduleRequest(
    @Size(max = 20, message = "Day cannot exceed 20 characters")
    String day,
    
    LocalTime startTime,
    
    LocalTime endTime,
    
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    String type,
    
    Boolean isActive
) {}
