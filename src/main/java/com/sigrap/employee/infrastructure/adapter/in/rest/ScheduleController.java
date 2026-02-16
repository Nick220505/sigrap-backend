package com.sigrap.employee.infrastructure.adapter.in.rest;

import com.sigrap.employee.application.port.in.CreateScheduleUseCase;
import com.sigrap.employee.application.port.in.DeleteScheduleUseCase;
import com.sigrap.employee.application.port.in.GetScheduleUseCase;
import com.sigrap.employee.application.port.in.UpdateScheduleUseCase;
import com.sigrap.employee.application.port.in.command.CreateScheduleCommand;
import com.sigrap.employee.application.port.in.command.UpdateScheduleCommand;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.user.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for schedule operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Employee Management", description = "APIs for managing employee attendance and schedules")
public class ScheduleController {
    
    private final CreateScheduleUseCase createScheduleUseCase;
    private final GetScheduleUseCase getScheduleUseCase;
    private final UpdateScheduleUseCase updateScheduleUseCase;
    private final DeleteScheduleUseCase deleteScheduleUseCase;
    private final ScheduleResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param createScheduleUseCase use case for creating schedules
     * @param getScheduleUseCase use case for retrieving schedules
     * @param updateScheduleUseCase use case for updating schedules
     * @param deleteScheduleUseCase use case for deleting schedules
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public ScheduleController(
            CreateScheduleUseCase createScheduleUseCase,
            GetScheduleUseCase getScheduleUseCase,
            UpdateScheduleUseCase updateScheduleUseCase,
            DeleteScheduleUseCase deleteScheduleUseCase,
            ScheduleResponseMapper responseMapper) {
        this.createScheduleUseCase = createScheduleUseCase;
        this.getScheduleUseCase = getScheduleUseCase;
        this.updateScheduleUseCase = updateScheduleUseCase;
        this.deleteScheduleUseCase = deleteScheduleUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new schedule.
     * POST /api/schedules
     *
     * @param request the schedule creation request
     * @return the created schedule response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new schedule",
        description = "Creates a new employee work schedule with specified day, time range, and type."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Schedule created successfully",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public ScheduleResponse create(@Valid @RequestBody ScheduleRequest request) {
        CreateScheduleCommand command = new CreateScheduleCommand(
            request.userId(),
            request.day(),
            request.startTime(),
            request.endTime(),
            request.type()
        );
        Schedule schedule = createScheduleUseCase.create(command);
        return responseMapper.toResponse(schedule);
    }
    
    /**
     * Retrieves a schedule by its ID.
     * GET /api/schedules/{id}
     *
     * @param id the schedule identifier
     * @return the schedule response with HTTP 200 status
     * @throws IllegalArgumentException if the schedule is not found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get schedule by ID",
        description = "Retrieves a specific schedule by its unique identifier."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Schedule found",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Schedule not found"
    )
    public ScheduleResponse getById(
        @Parameter(description = "Schedule unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        ScheduleId scheduleId = new ScheduleId(id);
        Schedule schedule = getScheduleUseCase.getById(scheduleId);
        return responseMapper.toResponse(schedule);
    }
    
    /**
     * Retrieves all schedules.
     * GET /api/schedules
     *
     * @return a list of all schedule responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all schedules",
        description = "Retrieves all employee schedules in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of schedules",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<ScheduleResponse> getAll() {
        return getScheduleUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all schedules for a specific user.
     * GET /api/schedules/user/{userId}
     *
     * @param userId the user identifier
     * @return a list of schedule responses for the user with HTTP 200 status
     */
    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get schedules by user ID",
        description = "Retrieves all schedules for a specific employee."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of schedules for the user",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<ScheduleResponse> getByUserId(
        @Parameter(description = "User unique identifier", required = true, example = "1")
        @PathVariable Long userId
    ) {
        UserId userIdObj = new UserId(userId);
        return getScheduleUseCase.getByUserId(userIdObj).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves schedules for a specific day of the week.
     * GET /api/schedules/day/{day}
     *
     * @param day the day of the week
     * @return a list of schedule responses for the specified day with HTTP 200 status
     */
    @GetMapping("/day/{day}")
    @Operation(
        summary = "Get schedules by day",
        description = "Retrieves all schedules for a specific day of the week."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of schedules for the specified day",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<ScheduleResponse> getByDay(
        @Parameter(description = "Day of the week", required = true, example = "MONDAY")
        @PathVariable String day
    ) {
        return getScheduleUseCase.getByDay(day).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing schedule.
     * PUT /api/schedules/{id}
     *
     * @param id the schedule identifier
     * @param request the schedule update request
     * @return the updated schedule response with HTTP 200 status
     * @throws IllegalArgumentException if the schedule is not found
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a schedule",
        description = "Updates an existing employee schedule. All fields are optional - only provided fields will be updated."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Schedule updated successfully",
        content = @Content(schema = @Schema(implementation = ScheduleResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Schedule not found"
    )
    public ScheduleResponse update(
            @Parameter(description = "Schedule unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        ScheduleId scheduleId = new ScheduleId(id);
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            request.day(),
            request.startTime(),
            request.endTime(),
            request.type(),
            request.isActive()
        );
        Schedule schedule = updateScheduleUseCase.update(scheduleId, command);
        return responseMapper.toResponse(schedule);
    }
    
    /**
     * Deletes a schedule by its ID.
     * DELETE /api/schedules/{id}
     *
     * @param id the schedule identifier
     * @return HTTP 204 No Content status
     * @throws IllegalArgumentException if the schedule is not found
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a schedule",
        description = "Deletes an employee schedule by its unique identifier."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Schedule deleted successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Schedule not found"
    )
    public void delete(
        @Parameter(description = "Schedule unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        ScheduleId scheduleId = new ScheduleId(id);
        deleteScheduleUseCase.delete(scheduleId);
    }
}
