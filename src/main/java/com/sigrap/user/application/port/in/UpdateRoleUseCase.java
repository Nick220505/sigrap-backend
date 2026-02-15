package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.UpdateRoleCommand;
import com.sigrap.user.domain.model.Role;

/**
 * Input port for updating an existing role.
 * This interface defines the use case for role updates.
 */
public interface UpdateRoleUseCase {
    
    /**
     * Updates an existing role with the provided command data.
     *
     * @param id the identifier of the role to update
     * @param command the command containing role update data
     * @return the updated role domain entity
     * @throws IllegalArgumentException if the role is not found
     * @throws IllegalArgumentException if the new name already exists for another role
     */
    Role update(Long id, UpdateRoleCommand command);
}
