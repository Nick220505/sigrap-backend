package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.domain.model.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting domain Role entities to RoleResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class RoleResponseMapper {
    
    /**
     * Converts a domain Role entity to a RoleResponse DTO.
     *
     * @param role the domain role entity
     * @return the role response DTO
     */
    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        
        return new RoleResponse(
            role.getId() != null ? role.getId().value() : null,
            role.getName().value(),
            role.getDescription(),
            role.getPermissions().stream()
                .map(permission -> permission.getName().value())
                .collect(Collectors.toSet())
        );
    }
}
