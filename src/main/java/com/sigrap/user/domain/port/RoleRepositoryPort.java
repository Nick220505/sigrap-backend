package com.sigrap.user.domain.port;

import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleId;
import com.sigrap.user.domain.model.RoleName;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Role domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface RoleRepositoryPort {
    
    /**
     * Saves a role (create or update).
     *
     * @param role the role to save
     * @return the saved role with generated ID if new
     */
    Role save(Role role);
    
    /**
     * Finds a role by its identifier.
     *
     * @param id the role identifier
     * @return an Optional containing the role if found, empty otherwise
     */
    Optional<Role> findById(RoleId id);
    
    /**
     * Finds a role by its name.
     *
     * @param name the role name
     * @return an Optional containing the role if found, empty otherwise
     */
    Optional<Role> findByName(RoleName name);
    
    /**
     * Retrieves all roles.
     *
     * @return a list of all roles
     */
    List<Role> findAll();
    
    /**
     * Checks if a role with the given name exists.
     *
     * @param name the role name to check
     * @return true if a role with the name exists, false otherwise
     */
    boolean existsByName(RoleName name);
    
    /**
     * Deletes a role by its identifier.
     *
     * @param id the role identifier
     */
    void deleteById(RoleId id);
}
