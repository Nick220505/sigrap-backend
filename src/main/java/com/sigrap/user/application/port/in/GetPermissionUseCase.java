package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.Permission;
import java.util.List;

/**
 * Input port for retrieving permissions.
 * This interface defines the use case for permission retrieval operations.
 */
public interface GetPermissionUseCase {
    
    /**
     * Retrieves a permission by its identifier.
     *
     * @param id the permission identifier
     * @return the permission domain entity
     * @throws IllegalArgumentException if the permission is not found
     */
    Permission getById(Long id);
    
    /**
     * Retrieves a permission by name.
     *
     * @param name the permission name
     * @return the permission domain entity
     * @throws IllegalArgumentException if the permission is not found
     */
    Permission getByName(String name);
    
    /**
     * Retrieves all permissions for a specific resource.
     *
     * @param resource the resource name
     * @return a list of permissions for the resource
     */
    List<Permission> getByResource(String resource);
    
    /**
     * Retrieves all permissions.
     *
     * @return a list of all permissions
     */
    List<Permission> getAll();
}
