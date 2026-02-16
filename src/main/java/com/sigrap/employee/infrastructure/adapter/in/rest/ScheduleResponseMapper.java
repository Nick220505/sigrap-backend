package com.sigrap.employee.infrastructure.adapter.in.rest;

import com.sigrap.employee.domain.model.Schedule;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Schedule entities to ScheduleResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class ScheduleResponseMapper {
    
    /**
     * Converts a domain Schedule entity to a ScheduleResponse DTO.
     *
     * @param schedule the domain schedule entity
     * @return the schedule response DTO
     */
    public ScheduleResponse toResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        
        return new ScheduleResponse(
            schedule.getId() != null ? schedule.getId().value() : null,
            schedule.getUserId().value(),
            schedule.getDay(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getType(),
            schedule.isActive(),
            schedule.getCreatedAt(),
            schedule.getUpdatedAt()
        );
    }
}
