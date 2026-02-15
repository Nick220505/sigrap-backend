package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.UpdatePermissionCommand;
import com.sigrap.user.domain.model.Permission;

/**
 * Input port for updating an existing permission.
 * This interface defines the use case for permission updates.
 */
public interface UpdatePermissionUseCase {
    
    /**
     * Updates an existing permission with the provided command data.
     *
     * @param id the identifier of the permission to update
     * @param command the command containing permission update data
     * @return the updated permission domain entity
     * @throws IllegalArgumentException if the permission is not found
     * @throws IllegalArgumentException if the new name already exists for another permission
     */
    Permission update(Long id, UpdatePermissionCommand command);
}
