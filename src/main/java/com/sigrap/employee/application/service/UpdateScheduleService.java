package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityUpdatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.employee.application.port.in.UpdateScheduleUseCase;
import com.sigrap.employee.application.port.in.command.UpdateScheduleCommand;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the UpdateScheduleUseCase.
 * This service orchestrates the update of an existing schedule by:
 * <ul>
 *   <li>Retrieving the existing schedule</li>
 *   <li>Applying updates through domain methods</li>
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
public class UpdateScheduleService implements UpdateScheduleUseCase {
    
    private final ScheduleRepositoryPort scheduleRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param scheduleRepository the repository port for schedule persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public UpdateScheduleService(ScheduleRepositoryPort scheduleRepository, EventPublisherPort eventPublisher) {
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Updates an existing schedule with the provided command data.
     * Only non-null fields in the command are applied to the schedule.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Schedule must exist</li>
     *   <li>Day must be valid if provided (enforced by Schedule entity)</li>
     *   <li>Times must be valid if provided (enforced by Schedule entity)</li>
     *   <li>End time must be after start time if both provided (enforced by Schedule entity)</li>
     * </ul>
     *
     * @param id the schedule identifier
     * @param command the command containing schedule update data
     * @return the updated schedule domain entity
     * @throws IllegalArgumentException if the schedule is not found
     * @throws IllegalArgumentException if the update data is invalid (from Schedule validation)
     */
    @Override
    public Schedule update(ScheduleId id, UpdateScheduleCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Retrieve existing schedule
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Schedule with ID " + id.value() + " not found"
            ));
        
        // Apply updates through domain methods (validates business rules)
        if (command.day() != null) {
            schedule.updateDay(command.day());
        }
        
        if (command.startTime() != null && command.endTime() != null) {
            schedule.updateTimes(command.startTime(), command.endTime());
        } else if (command.startTime() != null || command.endTime() != null) {
            // If only one time is provided, use existing value for the other
            schedule.updateTimes(
                command.startTime() != null ? command.startTime() : schedule.getStartTime(),
                command.endTime() != null ? command.endTime() : schedule.getEndTime()
            );
        }
        
        if (command.type() != null) {
            schedule.updateType(command.type());
        }
        
        if (command.isActive() != null) {
            if (command.isActive()) {
                schedule.activate();
            } else {
                schedule.deactivate();
            }
        }
        
        // Persist through port
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityUpdatedEvent(
            EntityType.SCHEDULE,
            savedSchedule.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Schedule updated",
            durationMs
        ));
        
        return savedSchedule;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
