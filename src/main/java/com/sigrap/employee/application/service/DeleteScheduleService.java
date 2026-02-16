package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityDeletedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.employee.application.port.in.DeleteScheduleUseCase;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the DeleteScheduleUseCase.
 * This service orchestrates the deletion of a schedule by:
 * <ul>
 *   <li>Verifying the schedule exists</li>
 *   <li>Deleting through the repository port</li>
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
public class DeleteScheduleService implements DeleteScheduleUseCase {
    
    private final ScheduleRepositoryPort scheduleRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param scheduleRepository the repository port for schedule persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public DeleteScheduleService(ScheduleRepositoryPort scheduleRepository, EventPublisherPort eventPublisher) {
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Deletes a schedule by its identifier.
     * Verifies the schedule exists before deletion.
     *
     * @param id the schedule identifier
     * @throws IllegalArgumentException if the schedule is not found
     */
    @Override
    public void delete(ScheduleId id) {
        long startTime = System.currentTimeMillis();
        
        // Verify schedule exists
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Schedule with ID " + id.value() + " not found"
            ));
        
        // Delete through port
        scheduleRepository.deleteById(id);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityDeletedEvent(
            EntityType.SCHEDULE,
            id.value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Schedule deleted for " + schedule.getDay(),
            durationMs
        ));
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
