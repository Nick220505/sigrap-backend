package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityUpdatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.employee.application.port.in.ClockOutUseCase;
import com.sigrap.employee.application.port.in.command.ClockOutCommand;
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the ClockOutUseCase.
 * This service orchestrates the clock-out process by:
 * <ul>
 *   <li>Retrieving the attendance record</li>
 *   <li>Recording the clock-out time</li>
 *   <li>Calculating total hours worked</li>
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
public class ClockOutService implements ClockOutUseCase {
    
    private final AttendanceRepositoryPort attendanceRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param attendanceRepository the repository port for attendance persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public ClockOutService(AttendanceRepositoryPort attendanceRepository, EventPublisherPort eventPublisher) {
        this.attendanceRepository = attendanceRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Records the clock-out time for an employee.
     * Updates the existing attendance record with the clock-out time and calculates total hours.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Attendance record must exist</li>
     *   <li>Employee must be clocked in</li>
     *   <li>Clock-out time must be after clock-in time</li>
     *   <li>Total hours are automatically calculated</li>
     * </ul>
     *
     * @param command the command containing clock-out data
     * @return the updated attendance domain entity with clock-out time and total hours
     * @throws IllegalArgumentException if the attendance record is not found
     * @throws IllegalStateException if the employee is not clocked in or already clocked out
     */
    @Override
    public Attendance clockOut(ClockOutCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Create value objects
        AttendanceId attendanceId = new AttendanceId(command.attendanceId());
        LocalDateTime clockOutTime = command.clockOutTime() != null ? 
            command.clockOutTime() : LocalDateTime.now();
        
        // Retrieve attendance record
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Attendance record with ID " + attendanceId.value() + " not found"
            ));
        
        // Record clock-out time (domain logic validates business rules)
        attendance.clockOut(clockOutTime);
        
        // Persist through port
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityUpdatedEvent(
            EntityType.ATTENDANCE,
            savedAttendance.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Employee clocked out at " + clockOutTime + 
                " (Total hours: " + savedAttendance.getTotalHours() + ")",
            durationMs
        ));
        
        return savedAttendance;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
