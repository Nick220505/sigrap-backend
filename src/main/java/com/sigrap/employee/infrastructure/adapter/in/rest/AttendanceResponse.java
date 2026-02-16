package com.sigrap.employee.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response DTO for attendance operations.
 * Represents the attendance data returned to REST clients.
 *
 * @param id the attendance identifier
 * @param userId the user identifier
 * @param date the date of attendance
 * @param clockInTime the clock-in time
 * @param clockOutTime the clock-out time
 * @param totalHours the total hours worked
 * @param status the attendance status
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@Schema(description = "Attendance record with all details")
public record AttendanceResponse(
    @Schema(description = "Attendance unique identifier", example = "1")
    Long id,
    
    @Schema(description = "User unique identifier", example = "1")
    Long userId,
    
    @Schema(description = "Attendance date", example = "2026-02-15T00:00:00")
    LocalDateTime date,
    
    @Schema(description = "Clock-in timestamp", example = "2026-02-15T08:00:00")
    LocalDateTime clockInTime,
    
    @Schema(description = "Clock-out timestamp", example = "2026-02-15T17:00:00")
    LocalDateTime clockOutTime,
    
    @Schema(description = "Total hours worked", example = "9.0")
    Double totalHours,
    
    @Schema(description = "Attendance status", example = "PRESENT")
    String status,
    
    @Schema(description = "Creation timestamp", example = "2026-02-15T08:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp", example = "2026-02-15T17:00:00")
    LocalDateTime updatedAt
) {}
