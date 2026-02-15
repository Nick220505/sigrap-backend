package com.sigrap.user.domain.port;

import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionId;
import com.sigrap.user.domain.model.PermissionName;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Permission domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface PermissionRepositoryPort {
    
    /**
     * Saves a permission (create or update).
     *
     * @param permission the permission to save
     * @return the saved permission with generated ID if new
     */
    Permission save(Permission permission);
    
    /**
     * Finds a permission by its identifier.
     *
     * @param id the permission identifier
     * @return an Optional containing the permission if found, empty otherwise
     */
    Optional<Permission> findById(PermissionId id);
    
    /**
     * Finds a permission by its name.
     *
     * @param name the permission name
     * @return an Optional containing the permission if found, empty otherwise
     */
    Optional<Permission> findByName(PermissionName name);
    
    /**
     * Finds all permissions for a specific resource.
     *
     * @param resource the resource name
     * @return a list of permissions for the resource
     */
    List<Permission> findByResource(String resource);
    
    /**
     * Retrieves all permissions.
     *
     * @return a list of all permissions
     */
    List<Permission> findAll();
    
    /**
     * Checks if a permission with the given name exists.
     *
     * @param name the permission name to check
     * @return true if a permission with the name exists, false otherwise
     */
    boolean existsByName(PermissionName name);
    
    /**
     * Deletes a permission by its identifier.
     *
     * @param id the permission identifier
     */
    void deleteById(PermissionId id);
}
