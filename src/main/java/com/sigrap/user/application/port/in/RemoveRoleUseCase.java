package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.User;

/**
 * Input port for removing a role from a user.
 * This interface defines the use case for role removal.
 */
public interface RemoveRoleUseCase {
    
    /**
     * Removes a role from a user.
     *
     * @param userId the identifier of the user
     * @param roleId the identifier of the role to remove
     * @return the updated user domain entity
     * @throws IllegalArgumentException if the user or role is not found
     */
    User removeRole(Long userId, Long roleId);
}
