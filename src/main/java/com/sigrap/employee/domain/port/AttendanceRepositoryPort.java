package com.sigrap.employee.domain.port;

import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.user.domain.model.UserId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Attendance domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface AttendanceRepositoryPort {
    
    /**
     * Saves an attendance record (create or update).
     *
     * @param attendance the attendance to save
     * @return the saved attendance with generated ID if new
     */
    Attendance save(Attendance attendance);
    
    /**
     * Finds an attendance record by its identifier.
     *
     * @param id the attendance identifier
     * @return an Optional containing the attendance if found, empty otherwise
     */
    Optional<Attendance> findById(AttendanceId id);
    
    /**
     * Retrieves all attendance records.
     *
     * @return a list of all attendance records
     */
    List<Attendance> findAll();
    
    /**
     * Finds all attendance records for a specific user.
     *
     * @param userId the user identifier
     * @return a list of attendance records for the user
     */
    List<Attendance> findByUserId(UserId userId);
    
    /**
     * Finds attendance records within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance records within the date range
     */
    List<Attendance> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Deletes an attendance record by its identifier.
     *
     * @param id the attendance identifier
     */
    void deleteById(AttendanceId id);
    
    /**
     * Counts the total number of attendance records.
     *
     * @return the total count of attendance records
     */
    long count();
    
    /**
     * Saves multiple attendance records at once.
     * Useful for batch operations.
     *
     * @param attendances the list of attendances to save
     * @return the list of saved attendances
     */
    List<Attendance> saveAll(List<Attendance> attendances);
}
