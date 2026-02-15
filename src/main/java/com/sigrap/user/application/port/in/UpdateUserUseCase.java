package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.UpdateUserCommand;
import com.sigrap.user.domain.model.User;

/**
 * Input port for updating an existing user.
 * This interface defines the use case for user updates.
 */
public interface UpdateUserUseCase {
    
    /**
     * Updates an existing user with the provided command data.
     *
     * @param id the identifier of the user to update
     * @param command the command containing user update data
     * @return the updated user domain entity
     * @throws IllegalArgumentException if the user is not found
     * @throws IllegalArgumentException if the new email already exists for another user
     */
    User update(Long id, UpdateUserCommand command);
}
