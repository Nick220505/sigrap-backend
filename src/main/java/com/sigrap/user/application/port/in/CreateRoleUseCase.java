package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.CreateRoleCommand;
import com.sigrap.user.domain.model.Role;

/**
 * Input port for creating a new role.
 * This interface defines the use case for role creation.
 */
public interface CreateRoleUseCase {
    
    /**
     * Creates a new role with the provided command data.
     *
     * @param command the command containing role creation data
     * @return the created role domain entity
     * @throws IllegalArgumentException if a role with the same name already exists
     */
    Role create(CreateRoleCommand command);
}
