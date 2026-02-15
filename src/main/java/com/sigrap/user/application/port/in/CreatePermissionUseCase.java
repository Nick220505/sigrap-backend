package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.CreatePermissionCommand;
import com.sigrap.user.domain.model.Permission;

/**
 * Input port for creating a new permission.
 * This interface defines the use case for permission creation.
 */
public interface CreatePermissionUseCase {
    
    /**
     * Creates a new permission with the provided command data.
     *
     * @param command the command containing permission creation data
     * @return the created permission domain entity
     * @throws IllegalArgumentException if a permission with the same name already exists
     */
    Permission create(CreatePermissionCommand command);
}
