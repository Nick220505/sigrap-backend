package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.User;

/**
 * Input port for disabling a user account.
 * This interface defines the use case for user account deactivation.
 */
public interface DisableUserUseCase {
    
    /**
     * Disables a user account by its identifier.
     *
     * @param id the identifier of the user to disable
     * @return the updated user domain entity
     * @throws IllegalArgumentException if the user is not found
     */
    User disable(Long id);
}
