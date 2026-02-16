package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for creating a new employee schedule")
public record ScheduleRequest(
    @Schema(
        description = "User unique identifier",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "User ID is required")
    Long userId,
    
    @Schema(
        description = "Day of the week",
        example = "MONDAY",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 20
    )
    @NotBlank(message = "Day is required")
    @Size(max = 20, message = "Day cannot exceed 20 characters")
    String day,
    
    @Schema(
        description = "Schedule start time",
        example = "08:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Start time is required")
    LocalTime startTime,
    
    @Schema(
        description = "Schedule end time",
        example = "17:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "End time is required")
    LocalTime endTime,
    
    @Schema(
        description = "Schedule type (e.g., REGULAR, OVERTIME, SHIFT)",
        example = "REGULAR",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50
    )
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    String type
) {}
