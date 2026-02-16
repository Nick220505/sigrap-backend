package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for updating an existing employee schedule. All fields are optional.")
public record UpdateScheduleRequest(
    @Schema(
        description = "Day of the week",
        example = "MONDAY",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 20
    )
    @Size(max = 20, message = "Day cannot exceed 20 characters")
    String day,
    
    @Schema(
        description = "Schedule start time",
        example = "08:00:00",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    LocalTime startTime,
    
    @Schema(
        description = "Schedule end time",
        example = "17:00:00",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    LocalTime endTime,
    
    @Schema(
        description = "Schedule type (e.g., REGULAR, OVERTIME, SHIFT)",
        example = "REGULAR",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 50
    )
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    String type,
    
    @Schema(
        description = "Whether the schedule is active",
        example = "true",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Boolean isActive
) {}
