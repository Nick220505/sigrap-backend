package com.sigrap.employee.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for clock-in operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param userId the user identifier (required)
 * @param clockInTime the clock-in time (required)
 */
public record ClockInRequest(
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotNull(message = "Clock-in time is required")
    LocalDateTime clockInTime
) {}
