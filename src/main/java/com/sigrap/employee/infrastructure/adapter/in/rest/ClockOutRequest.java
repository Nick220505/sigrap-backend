package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for clock-out operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param attendanceId the attendance record identifier (required)
 * @param clockOutTime the clock-out time (required)
 */
@Schema(description = "Request body for employee clock-out operation")
public record ClockOutRequest(
    @Schema(
        description = "Attendance record unique identifier",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Attendance ID is required")
    Long attendanceId,
    
    @Schema(
        description = "Clock-out timestamp",
        example = "2026-02-15T17:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Clock-out time is required")
    LocalDateTime clockOutTime
) {}
