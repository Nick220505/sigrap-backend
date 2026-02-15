package com.sigrap.user.application.port.in;

/**
 * Input port for deleting a user.
 * This interface defines the use case for user deletion.
 */
public interface DeleteUserUseCase {
    
    /**
     * Deletes a user by its identifier.
     *
     * @param id the identifier of the user to delete
     * @throws IllegalArgumentException if the user is not found
     */
    void delete(Long id);
}
