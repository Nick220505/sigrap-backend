package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.employee.application.port.in.ClockInUseCase;
import com.sigrap.employee.application.port.in.command.ClockInCommand;
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceStatus;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the ClockInUseCase.
 * This service orchestrates the clock-in process by:
 * <ul>
 *   <li>Creating or updating an attendance record</li>
 *   <li>Recording the clock-in time</li>
 *   <li>Persisting through the repository port</li>
 *   <li>Publishing domain events for audit logging</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class ClockInService implements ClockInUseCase {
    
    private final AttendanceRepositoryPort attendanceRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param attendanceRepository the repository port for attendance persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public ClockInService(AttendanceRepositoryPort attendanceRepository, EventPublisherPort eventPublisher) {
        this.attendanceRepository = attendanceRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Records the clock-in time for an employee.
     * Creates a new attendance record with the clock-in time.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>User ID must be valid</li>
     *   <li>Clock-in time must be provided</li>
     *   <li>Creates new attendance record with PRESENT status</li>
     * </ul>
     *
     * @param command the command containing clock-in data
     * @return the created attendance domain entity with clock-in time recorded
     * @throws IllegalArgumentException if the user ID or clock-in time is invalid
     */
    @Override
    public Attendance clockIn(ClockInCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Create value objects
        UserId userId = new UserId(command.userId());
        LocalDateTime clockInTime = command.clockInTime() != null ? 
            command.clockInTime() : LocalDateTime.now();
        
        // Create new attendance record
        Attendance attendance = new Attendance(
            userId,
            clockInTime.toLocalDate().atStartOfDay(),
            AttendanceStatus.PRESENT
        );
        
        // Record clock-in time
        attendance.clockIn(clockInTime);
        
        // Persist through port
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.ATTENDANCE,
            savedAttendance.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Employee clocked in at " + clockInTime,
            durationMs
        ));
        
        return savedAttendance;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
