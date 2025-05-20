package com.sigrap.employee.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for attendance management operations.
 * Provides endpoints for attendance-related functionality.
 *
 * <p>This controller includes endpoints for:
 * <ul>
 *   <li>Clock-in/Clock-out operations</li>
 *   <li>Attendance reporting</li>
 *   <li>Attendance search functionality</li>
 *   <li>Status management</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(
  name = "Attendance Management",
  description = "Endpoints for managing attendance"
)
public class AttendanceController {

  private final AttendanceService attendanceService;

  /**
   * Retrieves all attendance records.
   *
   * @return List of AttendanceInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all attendance records",
    description = "Retrieves a list of all attendance records in the system"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved list of attendance records"
  )
  public List<AttendanceInfo> findAll() {
    return attendanceService.findAll();
  }

  /**
   * Records a clock-in for a user.
   *
   * @param clockInData The clock-in data
   * @return AttendanceInfo containing the created attendance record
   */
  @PostMapping("/clock-in")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Record clock-in",
    description = "Records a clock-in for a user"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully recorded clock-in"
  )
  @ApiResponse(responseCode = "404", description = "User not found")
  public AttendanceInfo clockIn(
    @Parameter(
      description = "Clock-in data"
    ) @Valid @RequestBody ClockInData clockInData
  ) {
    return attendanceService.clockIn(
      clockInData.getUserId(),
      clockInData.getTimestamp()
    );
  }

  /**
   * Records a clock-out for a user.
   *
   * @param clockOutData The clock-out data
   * @return AttendanceInfo containing the updated attendance record
   */
  @PutMapping("/clock-out")
  @Operation(
    summary = "Record clock-out",
    description = "Records a clock-out for a user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully recorded clock-out"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Attendance record not found"
  )
  public AttendanceInfo clockOut(
    @Parameter(
      description = "Clock-out data"
    ) @Valid @RequestBody ClockOutData clockOutData
  ) {
    return attendanceService.clockOut(
      clockOutData.getAttendanceId(),
      clockOutData.getTimestamp()
    );
  }

  /**
   * Finds all attendance records for a specific user.
   *
   * @param userId The ID of the user
   * @return List of AttendanceInfo DTOs
   */
  @GetMapping("/user/{userId}")
  @Operation(
    summary = "Find attendance by user",
    description = "Retrieves all attendance records for a specific user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved attendance records"
  )
  public List<AttendanceInfo> findByUserId(
    @Parameter(description = "ID of the user") @PathVariable Long userId
  ) {
    return attendanceService.findByUserId(userId);
  }

  /**
   * Generates an attendance report for a specific user between two dates.
   *
   * @param userId The ID of the user
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of AttendanceInfo DTOs
   */
  @GetMapping("/report")
  @Operation(
    summary = "Generate attendance report",
    description = "Generates an attendance report for a specific user between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully generated attendance report"
  )
  public List<AttendanceInfo> generateAttendanceReport(
    @Parameter(description = "ID of the user") @RequestParam Long userId,
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return attendanceService.generateAttendanceReport(
      userId,
      startDate,
      endDate
    );
  }

  /**
   * Updates an attendance record's status.
   *
   * @param id The ID of the attendance record
   * @param status The new status
   * @param notes Optional notes about the status change
   * @return AttendanceInfo containing the updated attendance record
   */
  @PutMapping("/{id}/status")
  @Operation(
    summary = "Update attendance status",
    description = "Updates the status of an attendance record"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully updated attendance status"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Attendance record not found"
  )
  public AttendanceInfo updateStatus(
    @Parameter(
      description = "ID of the attendance record"
    ) @PathVariable Long id,
    @Parameter(
      description = "New status"
    ) @RequestParam AttendanceStatus status,
    @Parameter(description = "Optional notes") @RequestParam(
      required = false
    ) String notes
  ) {
    return attendanceService.updateStatus(id, status, notes);
  }

  /**
   * Finds all attendance records with a specific status.
   *
   * @param status The status to search for
   * @return List of AttendanceInfo DTOs
   */
  @GetMapping("/status/{status}")
  @Operation(
    summary = "Find attendance by status",
    description = "Retrieves all attendance records with a specific status"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved attendance records"
  )
  public List<AttendanceInfo> findByStatus(
    @Parameter(
      description = "Status to search for"
    ) @PathVariable AttendanceStatus status
  ) {
    return attendanceService.findByStatus(status);
  }

  /**
   * Finds all attendance records between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of AttendanceInfo DTOs
   */
  @GetMapping("/date-range")
  @Operation(
    summary = "Find attendance by date range",
    description = "Retrieves all attendance records between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved attendance records"
  )
  public List<AttendanceInfo> findByDateRange(
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return attendanceService.findByDateRange(startDate, endDate);
  }
}
