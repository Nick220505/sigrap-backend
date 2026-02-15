package com.sigrap.user.application.port.in;

/**
 * Input port for deleting a permission.
 * This interface defines the use case for permission deletion.
 */
public interface DeletePermissionUseCase {
    
    /**
     * Deletes a permission by its identifier.
     *
     * @param id the identifier of the permission to delete
     * @throws IllegalArgumentException if the permission is not found
     */
    void delete(Long id);
}
