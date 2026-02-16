package com.sigrap.employee.infrastructure.adapter.in.rest;

import com.sigrap.employee.domain.model.Attendance;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Attendance entities to AttendanceResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class AttendanceResponseMapper {
    
    /**
     * Converts a domain Attendance entity to an AttendanceResponse DTO.
     *
     * @param attendance the domain attendance entity
     * @return the attendance response DTO
     */
    public AttendanceResponse toResponse(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        
        return new AttendanceResponse(
            attendance.getId() != null ? attendance.getId().value() : null,
            attendance.getUserId().value(),
            attendance.getDate(),
            attendance.getClockInTime(),
            attendance.getClockOutTime(),
            attendance.getTotalHours(),
            attendance.getStatus().name(),
            attendance.getCreatedAt(),
            attendance.getUpdatedAt()
        );
    }
}
