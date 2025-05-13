package com.sigrap.employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for activity log management.
 * Provides endpoints for logging and retrieving activity information.
 *
 * <p>This controller provides:
 * <ul>
 *   <li>Activity logging endpoint</li>
 *   <li>Activity search endpoints</li>
 *   <li>Report generation endpoint</li>
 *   <li>OpenAPI documentation</li>
 * </ul></p>
 *
 * <p>Key features:
 * <ul>
 *   <li>RESTful design</li>
 *   <li>Input validation</li>
 *   <li>Comprehensive documentation</li>
 *   <li>Error handling</li>
 * </ul></p>
 *
 * @see ActivityLogService
 * @see ActivityLogData
 * @see ActivityLogInfo
 */
@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
@Tag(
  name = "Activity Logs",
  description = "Activity logging and monitoring endpoints"
)
public class ActivityLogController {

  private final ActivityLogService activityLogService;

  /**
   * Retrieves all activity logs.
   *
   * @return List of all activity logs
   */
  @GetMapping
  @Operation(summary = "Get all activity logs")
  @ApiResponse(
    responseCode = "200",
    description = "Activity logs retrieved successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  public List<ActivityLogInfo> findAll() {
    return activityLogService.findAll();
  }

  /**
   * Creates a new activity log entry.
   *
   * @param data The activity data to log
   * @return ActivityLogInfo containing the created log's information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Log a new activity")
  @ApiResponse(
    responseCode = "201",
    description = "Activity logged successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid activity data provided"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Referenced employee not found"
  )
  public ActivityLogInfo logActivity(
    @Valid @RequestBody @Parameter(
      description = "Activity data to log",
      required = true
    ) ActivityLogData data
  ) {
    return activityLogService.logActivity(data);
  }

  /**
   * Deletes an activity log by its ID.
   *
   * @param id ID of the activity log to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete an activity log")
  @ApiResponse(
    responseCode = "204",
    description = "Activity log deleted successfully"
  )
  @ApiResponse(responseCode = "404", description = "Activity log not found")
  public void delete(
    @PathVariable @Parameter(
      description = "ID of the activity log to delete",
      required = true
    ) Long id
  ) {
    activityLogService.deleteById(id);
  }

  /**
   * Deletes multiple activity logs by their IDs.
   *
   * @param ids List of activity log IDs to delete
   */
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete multiple activity logs")
  @ApiResponse(
    responseCode = "204",
    description = "Activity logs deleted successfully"
  )
  @ApiResponse(responseCode = "400", description = "Invalid IDs provided")
  public void deleteAllById(
    @RequestBody @Parameter(
      description = "List of activity log IDs to delete",
      required = true
    ) List<Long> ids
  ) {
    activityLogService.deleteAllById(ids);
  }

  /**
   * Retrieves all activity logs for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of activity logs for the employee
   */
  @GetMapping("/employee/{employeeId}")
  @Operation(summary = "Get activity logs for an employee")
  @ApiResponse(
    responseCode = "200",
    description = "Activity logs retrieved successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  public List<ActivityLogInfo> findByEmployeeId(
    @PathVariable @Parameter(
      description = "ID of the employee",
      required = true
    ) Long employeeId
  ) {
    return activityLogService.findByEmployeeId(employeeId);
  }

  /**
   * Retrieves all activity logs of a specific type.
   *
   * @param actionType Type of action to filter by
   * @return List of activity logs of the specified type
   */
  @GetMapping("/action-type/{actionType}")
  @Operation(summary = "Get activity logs by action type")
  @ApiResponse(
    responseCode = "200",
    description = "Activity logs retrieved successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  public List<ActivityLogInfo> findByActionType(
    @PathVariable @Parameter(
      description = "Type of action",
      required = true
    ) ActivityLog.ActionType actionType
  ) {
    return activityLogService.findByActionType(actionType);
  }

  /**
   * Retrieves all activity logs from a specific module.
   *
   * @param moduleName Name of the module to filter by
   * @return List of activity logs from the specified module
   */
  @GetMapping("/module/{moduleName}")
  @Operation(summary = "Get activity logs by module name")
  @ApiResponse(
    responseCode = "200",
    description = "Activity logs retrieved successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  public List<ActivityLogInfo> findByModuleName(
    @PathVariable @Parameter(
      description = "Name of the module",
      required = true
    ) String moduleName
  ) {
    return activityLogService.findByModuleName(moduleName);
  }

  /**
   * Generates an activity report for a date range.
   *
   * @param employeeId Optional ID of the employee to filter by
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of activity logs matching the criteria
   */
  @GetMapping("/report")
  @Operation(summary = "Generate activity report for a date range")
  @ApiResponse(
    responseCode = "200",
    description = "Activity report generated successfully",
    content = @Content(schema = @Schema(implementation = ActivityLogInfo.class))
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid date range provided"
  )
  public List<ActivityLogInfo> generateActivityReport(
    @RequestParam(required = false) @Parameter(
      description = "ID of the employee (optional)"
    ) Long employeeId,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) @Parameter(
      description = "Start date (ISO format)",
      required = true
    ) LocalDateTime startDate,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) @Parameter(
      description = "End date (ISO format)",
      required = true
    ) LocalDateTime endDate
  ) {
    return activityLogService.generateActivityReport(
      employeeId,
      startDate,
      endDate
    );
  }
}
