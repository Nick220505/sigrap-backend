package com.sigrap.employee.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for schedule management operations.
 * Provides endpoints for schedule-related functionality.
 *
 * <p>This controller includes endpoints for:
 * <ul>
 *   <li>Schedule CRUD operations</li>
 *   <li>Weekly schedule generation</li>
 *   <li>Schedule copying</li>
 *   <li>Schedule search functionality</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(
  name = "Schedule Management",
  description = "Endpoints for managing schedules"
)
public class ScheduleController {

  private final ScheduleService scheduleService;

  /**
   * Retrieves all schedules.
   *
   * @return List of ScheduleInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all schedules",
    description = "Retrieves a list of all schedules in the system"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved list of schedules"
  )
  public List<ScheduleInfo> findAll() {
    return scheduleService.findAll();
  }

  /**
   * Retrieves a schedule by its ID.
   *
   * @param id The ID of the schedule to retrieve
   * @return ScheduleInfo containing the schedule's information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get schedule by ID",
    description = "Retrieves a schedule's information by its ID"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved schedule information"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Schedule not found with the given ID"
  )
  public ScheduleInfo findById(
    @Parameter(
      description = "ID of the schedule to retrieve"
    ) @PathVariable Long id
  ) {
    return scheduleService.findById(id);
  }

  /**
   * Creates a new schedule.
   *
   * @param scheduleData The data for the new schedule
   * @return ScheduleInfo containing the created schedule's information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create new schedule",
    description = "Creates a new schedule with the provided information"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully created new schedule"
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid schedule data provided"
  )
  public ScheduleInfo create(
    @Parameter(
      description = "Schedule data for creation"
    ) @Valid @RequestBody ScheduleData scheduleData
  ) {
    return scheduleService.create(scheduleData);
  }

  /**
   * Updates an existing schedule.
   *
   * @param id The ID of the schedule to update
   * @param scheduleData The new data for the schedule
   * @return ScheduleInfo containing the updated schedule's information
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update schedule",
    description = "Updates an existing schedule's information"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully updated schedule information"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Schedule not found with the given ID"
  )
  public ScheduleInfo update(
    @Parameter(
      description = "ID of the schedule to update"
    ) @PathVariable Long id,
    @Parameter(
      description = "Updated schedule data"
    ) @Valid @RequestBody ScheduleData scheduleData
  ) {
    return scheduleService.update(id, scheduleData);
  }

  /**
   * Deletes a schedule.
   *
   * @param id The ID of the schedule to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete schedule",
    description = "Deletes a schedule from the system"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Successfully deleted schedule"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Schedule not found with the given ID"
  )
  public void delete(
    @Parameter(
      description = "ID of the schedule to delete"
    ) @PathVariable Long id
  ) {
    scheduleService.delete(id);
  }

  /**
   * Finds all schedules for a specific user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs
   */
  @GetMapping("/user/{userId}")
  @Operation(
    summary = "Find schedules by user",
    description = "Retrieves all schedules for a specific user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved schedules"
  )
  public List<ScheduleInfo> findByUserId(
    @Parameter(description = "ID of the user") @PathVariable Long userId
  ) {
    return scheduleService.findByUserId(userId);
  }

  /**
   * Generates a weekly schedule for a user.
   *
   * @param userId The ID of the user
   * @param scheduleData The base schedule data to use
   * @return List of ScheduleInfo DTOs for the generated schedules
   */
  @PostMapping("/generate-weekly/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Generate weekly schedule",
    description = "Generates a weekly schedule for a user"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully generated weekly schedule"
  )
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the given ID"
  )
  public List<ScheduleInfo> generateWeeklySchedule(
    @Parameter(description = "ID of the user") @PathVariable Long userId,
    @Parameter(
      description = "Base schedule data"
    ) @Valid @RequestBody ScheduleData scheduleData
  ) {
    return scheduleService.generateWeeklySchedule(userId, scheduleData);
  }

  /**
   * Copies schedules from the previous week for a user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs for the copied schedules
   */
  @PostMapping("/copy-previous-week/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Copy previous week's schedule for user",
    description = "Copies schedules from the previous week for a user"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully copied schedules"
  )
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the given ID"
  )
  public List<ScheduleInfo> copyScheduleFromPreviousWeek(
    @Parameter(description = "ID of the user") @PathVariable Long userId
  ) {
    return scheduleService.copyScheduleFromPreviousWeek(userId);
  }

  /**
   * Finds all active schedules for a specific user.
   *
   * @param userId The ID of the user
   * @return List of ScheduleInfo DTOs
   */
  @GetMapping("/active/user/{userId}")
  @Operation(
    summary = "Find active schedules by user",
    description = "Retrieves all active schedules for a specific user"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved schedules"
  )
  public List<ScheduleInfo> findActiveSchedulesByUserId(
    @Parameter(description = "ID of the user") @PathVariable Long userId
  ) {
    return scheduleService.findActiveSchedulesByUserId(userId);
  }

  /**
   * Finds all active schedules for a specific day.
   *
   * @param day The day to search for
   * @return List of ScheduleInfo DTOs
   */
  @GetMapping("/active/day/{day}")
  @Operation(
    summary = "Find active schedules by day",
    description = "Retrieves all active schedules for a specific day"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved schedules"
  )
  public List<ScheduleInfo> findActiveSchedulesByDay(
    @Parameter(description = "Day of the week") @PathVariable String day
  ) {
    return scheduleService.findActiveSchedulesByDay(day);
  }
}
