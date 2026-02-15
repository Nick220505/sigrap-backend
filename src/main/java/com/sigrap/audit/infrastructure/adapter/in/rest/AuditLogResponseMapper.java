package com.sigrap.audit.infrastructure.adapter.in.rest;

import com.sigrap.audit.domain.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting domain AuditLog to REST response DTO.
 * 
 * <p>This mapper handles the translation from the domain model to the
 * REST API response format.</p>
 */
@Mapper(componentModel = "spring")
public interface AuditLogResponseMapper {
    
    /**
     * Converts a domain AuditLog to a response DTO.
     * 
     * @param auditLog The domain audit log
     * @return The response DTO
     */
    @Mapping(target = "id", source = "id.value")
    AuditLogResponse toResponse(AuditLog auditLog);
}
