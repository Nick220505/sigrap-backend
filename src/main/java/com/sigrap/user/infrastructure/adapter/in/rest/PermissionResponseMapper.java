package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.domain.model.Permission;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Permission entities to PermissionResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class PermissionResponseMapper {
    
    /**
     * Converts a domain Permission entity to a PermissionResponse DTO.
     *
     * @param permission the domain permission entity
     * @return the permission response DTO
     */
    public PermissionResponse toResponse(Permission permission) {
        if (permission == null) {
            return null;
        }
        
        return new PermissionResponse(
            permission.getId() != null ? permission.getId().value() : null,
            permission.getName().value(),
            permission.getResource(),
            permission.getAction()
        );
    }
}
