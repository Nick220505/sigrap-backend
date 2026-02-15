package com.sigrap.user.application.port.in;

/**
 * Input port for deleting a role.
 * This interface defines the use case for role deletion.
 */
public interface DeleteRoleUseCase {
    
    /**
     * Deletes a role by its identifier.
     *
     * @param id the identifier of the role to delete
     * @throws IllegalArgumentException if the role is not found
     */
    void delete(Long id);
}
