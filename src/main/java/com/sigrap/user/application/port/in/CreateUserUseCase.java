package com.sigrap.user.application.port.in;

import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.domain.model.User;

/**
 * Input port for creating a new user.
 * This interface defines the use case for user creation.
 */
public interface CreateUserUseCase {
    
    /**
     * Creates a new user with the provided command data.
     *
     * @param command the command containing user creation data
     * @return the created user domain entity
     * @throws IllegalArgumentException if a user with the same username or email already exists
     */
    User create(CreateUserCommand command);
}
