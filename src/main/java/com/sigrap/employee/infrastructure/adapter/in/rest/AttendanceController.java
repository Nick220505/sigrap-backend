package com.sigrap.employee.infrastructure.adapter.in.rest;

import com.sigrap.employee.application.port.in.ClockInUseCase;
import com.sigrap.employee.application.port.in.ClockOutUseCase;
import com.sigrap.employee.application.port.in.GetAttendanceUseCase;
import com.sigrap.employee.application.port.in.command.ClockInCommand;
import com.sigrap.employee.application.port.in.command.ClockOutCommand;
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.user.domain.model.UserId;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for attendance operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    
    private final ClockInUseCase clockInUseCase;
    private final ClockOutUseCase clockOutUseCase;
    private final GetAttendanceUseCase getAttendanceUseCase;
    private final AttendanceResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     *
     * @param clockInUseCase use case for clocking in
     * @param clockOutUseCase use case for clocking out
     * @param getAttendanceUseCase use case for retrieving attendances
     * @param responseMapper mapper for converting domain entities to response DTOs
     */
    public AttendanceController(
            ClockInUseCase clockInUseCase,
            ClockOutUseCase clockOutUseCase,
            GetAttendanceUseCase getAttendanceUseCase,
            AttendanceResponseMapper responseMapper) {
        this.clockInUseCase = clockInUseCase;
        this.clockOutUseCase = clockOutUseCase;
        this.getAttendanceUseCase = getAttendanceUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Records clock-in time for an employee.
     * POST /api/attendances/clock-in
     *
     * @param request the clock-in request
     * @return the created/updated attendance response with HTTP 201 status
     */
    @PostMapping("/clock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse clockIn(@Valid @RequestBody ClockInRequest request) {
        ClockInCommand command = new ClockInCommand(
            request.userId(),
            request.clockInTime()
        );
        Attendance attendance = clockInUseCase.clockIn(command);
        return responseMapper.toResponse(attendance);
    }
    
    /**
     * Records clock-out time for an employee.
     * PUT /api/attendance/clock-out
     *
     * @param request the clock-out request
     * @return the updated attendance response with HTTP 200 status
     */
    @PutMapping("/clock-out")
    public AttendanceResponse clockOut(@Valid @RequestBody ClockOutRequest request) {
        ClockOutCommand command = new ClockOutCommand(
            request.attendanceId(),
            request.clockOutTime()
        );
        Attendance attendance = clockOutUseCase.clockOut(command);
        return responseMapper.toResponse(attendance);
    }
    
    /**
     * Retrieves an attendance record by its ID.
     * GET /api/attendances/{id}
     *
     * @param id the attendance identifier
     * @return the attendance response with HTTP 200 status
     * @throws IllegalArgumentException if the attendance is not found
     */
    @GetMapping("/{id}")
    public AttendanceResponse getById(@PathVariable Long id) {
        AttendanceId attendanceId = new AttendanceId(id);
        Attendance attendance = getAttendanceUseCase.getById(attendanceId);
        return responseMapper.toResponse(attendance);
    }
    
    /**
     * Retrieves all attendance records.
     * GET /api/attendances
     *
     * @return a list of all attendance responses with HTTP 200 status
     */
    @GetMapping
    public List<AttendanceResponse> getAll() {
        return getAttendanceUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all attendance records for a specific user.
     * GET /api/attendances/user/{userId}
     *
     * @param userId the user identifier
     * @return a list of attendance responses for the user with HTTP 200 status
     */
    @GetMapping("/user/{userId}")
    public List<AttendanceResponse> getByUserId(@PathVariable Long userId) {
        UserId userIdObj = new UserId(userId);
        return getAttendanceUseCase.getByUserId(userIdObj).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves attendance records within a date range.
     * GET /api/attendance/date-range?startDate={startDate}&endDate={endDate}
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance responses within the date range with HTTP 200 status
     */
    @GetMapping("/date-range")
    public List<AttendanceResponse> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return getAttendanceUseCase.getByDateRange(startDate, endDate).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Generates an attendance report for a specific user between two dates.
     * GET /api/attendance/report?userId={userId}&startDate={startDate}&endDate={endDate}
     *
     * @param userId the user identifier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance responses for the user within the date range with HTTP 200 status
     */
    @GetMapping("/report")
    public List<AttendanceResponse> generateAttendanceReport(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        UserId userIdObj = new UserId(userId);
        return getAttendanceUseCase.getByUserId(userIdObj).stream()
            .filter(attendance -> {
                LocalDateTime clockIn = attendance.getClockInTime();
                return !clockIn.isBefore(startDate) && !clockIn.isAfter(endDate);
            })
            .map(responseMapper::toResponse)
            .toList();
    }
}
