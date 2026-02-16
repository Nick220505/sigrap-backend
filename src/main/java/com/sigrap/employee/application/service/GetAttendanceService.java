package com.sigrap.employee.application.service;

import com.sigrap.employee.application.port.in.GetAttendanceUseCase;
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetAttendanceUseCase.
 * This service provides read operations for attendance records.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for data retrieval</li>
 *   <li>Read-only operations with @Transactional(readOnly = true)</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class GetAttendanceService implements GetAttendanceUseCase {
    
    private final AttendanceRepositoryPort attendanceRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param attendanceRepository the repository port for attendance retrieval
     */
    public GetAttendanceService(AttendanceRepositoryPort attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }
    
    /**
     * Retrieves an attendance record by its identifier.
     *
     * @param id the attendance identifier
     * @return the attendance domain entity
     * @throws IllegalArgumentException if the attendance is not found
     */
    @Override
    public Attendance getById(AttendanceId id) {
        return attendanceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Attendance with ID " + id.value() + " not found"
            ));
    }
    
    /**
     * Retrieves an attendance record by its identifier, returning an Optional.
     *
     * @param id the attendance identifier
     * @return an Optional containing the attendance if found, empty otherwise
     */
    @Override
    public Optional<Attendance> findById(AttendanceId id) {
        return attendanceRepository.findById(id);
    }
    
    /**
     * Retrieves all attendance records.
     *
     * @return a list of all attendance domain entities
     */
    @Override
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }
    
    /**
     * Retrieves all attendance records for a specific user.
     *
     * @param userId the user identifier
     * @return a list of attendance records for the user
     */
    @Override
    public List<Attendance> getByUserId(UserId userId) {
        return attendanceRepository.findByUserId(userId);
    }
    
    /**
     * Retrieves attendance records within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of attendance records within the date range
     */
    @Override
    public List<Attendance> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceRepository.findByDateRange(startDate, endDate);
    }
}
