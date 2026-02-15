package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.User;

/**
 * Input port for assigning a role to a user.
 * This interface defines the use case for role assignment.
 */
public interface AssignRoleUseCase {
    
    /**
     * Assigns a role to a user.
     *
     * @param userId the identifier of the user
     * @param roleId the identifier of the role to assign
     * @return the updated user domain entity
     * @throws IllegalArgumentException if the user or role is not found
     */
    User assignRole(Long userId, Long roleId);
}
