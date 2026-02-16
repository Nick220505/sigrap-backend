package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Request DTO for clock-in operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param userId the user identifier (required)
 * @param clockInTime the clock-in time (required)
 */
@Schema(description = "Request body for employee clock-in operation")
public record ClockInRequest(
    @Schema(
        description = "User unique identifier",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "User ID is required")
    Long userId,
    
    @Schema(
        description = "Clock-in timestamp",
        example = "2026-02-15T08:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Clock-in time is required")
    LocalDateTime clockInTime
) {}
