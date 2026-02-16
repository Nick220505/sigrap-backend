package com.sigrap.employee.application.port.in;

import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.user.domain.model.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving attendance records.
 * This interface defines the use cases for attendance retrieval operations.
 */
public interface GetAttendanceUseCase {
    
    /**
     * Retrieves an attendance record by its identifier.
     *
     * @param id the attendance identifier
     * @return the attendance domain entity
     * @throws IllegalArgumentException if the attendance is not found
     */
    Attendance getById(AttendanceId id);
    
    /**
     * Retrieves an attendance record by its identifier, returning an Optional.
     *
     * @param id the attendance identifier
     * @return an Optional containing the attendance if found, empty otherwise
     */
    Optional<Attendance> findById(AttendanceId id);
    
    /**
     * Retrieves all attendance records.
     *
     * @return a list of all attendance domain entities
     */
    List<Attendance> getAll();
    
    /**
     * Retrieves all attendance records for a specific user.
     *
     * @param userId the user identifier
     * @return a list of attendance records for the user
     */
    List<Attendance> getByUserId(UserId userId);
    
    /**
     * Retrieves attendance records within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance records within the date range
     */
    List<Attendance> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
