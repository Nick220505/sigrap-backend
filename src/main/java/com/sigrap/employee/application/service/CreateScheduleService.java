package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.employee.application.port.in.CreateScheduleUseCase;
import com.sigrap.employee.application.port.in.command.CreateScheduleCommand;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the CreateScheduleUseCase.
 * This service orchestrates the creation of a new schedule by:
 * <ul>
 *   <li>Validating business rules</li>
 *   <li>Creating the domain entity</li>
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
public class CreateScheduleService implements CreateScheduleUseCase {
    
    private final ScheduleRepositoryPort scheduleRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param scheduleRepository the repository port for schedule persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public CreateScheduleService(ScheduleRepositoryPort scheduleRepository, EventPublisherPort eventPublisher) {
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a new schedule with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>User ID must be valid</li>
     *   <li>Day, start time, and end time must be valid (enforced by Schedule entity)</li>
     *   <li>End time must be after start time (enforced by Schedule entity)</li>
     * </ul>
     *
     * @param command the command containing schedule creation data
     * @return the created schedule domain entity with generated ID
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws IllegalArgumentException if the schedule data is invalid (from Schedule validation)
     */
    @Override
    public Schedule create(CreateScheduleCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Create value objects
        UserId userId = new UserId(command.userId());
        
        // Create domain entity (validates business rules)
        Schedule schedule = new Schedule(
            userId,
            command.day(),
            command.startTime(),
            command.endTime(),
            command.type()
        );
        
        // Persist through port and return with generated ID
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.SCHEDULE,
            savedSchedule.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Schedule created for " + savedSchedule.getDay() + 
                " (" + savedSchedule.getStartTime() + " - " + savedSchedule.getEndTime() + ")",
            durationMs
        ));
        
        return savedSchedule;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
