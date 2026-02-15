package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting domain User entities to UserResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class UserResponseMapper {
    
    /**
     * Converts a domain User entity to a UserResponse DTO.
     *
     * @param user the domain user entity
     * @return the user response DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserResponse(
            user.getId() != null ? user.getId().value() : null,
            user.getUsername().value(),
            user.getEmail().value(),
            user.isEnabled(),
            user.getRoles().stream()
                .map(role -> role.getName().value())
                .collect(Collectors.toSet()),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
