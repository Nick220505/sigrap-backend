package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.User;

/**
 * Input port for enabling a user account.
 * This interface defines the use case for user account activation.
 */
public interface EnableUserUseCase {
    
    /**
     * Enables a user account by its identifier.
     *
     * @param id the identifier of the user to enable
     * @return the updated user domain entity
     * @throws IllegalArgumentException if the user is not found
     */
    User enable(Long id);
}
