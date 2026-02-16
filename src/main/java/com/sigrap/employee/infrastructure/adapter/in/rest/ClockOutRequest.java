package com.sigrap.employee.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for clock-out operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param attendanceId the attendance record identifier (required)
 * @param clockOutTime the clock-out time (required)
 */
public record ClockOutRequest(
    @NotNull(message = "Attendance ID is required")
    Long attendanceId,
    
    @NotNull(message = "Clock-out time is required")
    LocalDateTime clockOutTime
) {}
