package com.sigrap.user.application.port.in;

import com.sigrap.user.domain.model.Role;
import java.util.List;

/**
 * Input port for retrieving roles.
 * This interface defines the use case for role retrieval operations.
 */
public interface GetRoleUseCase {
    
    /**
     * Retrieves a role by its identifier.
     *
     * @param id the role identifier
     * @return the role domain entity
     * @throws IllegalArgumentException if the role is not found
     */
    Role getById(Long id);
    
    /**
     * Retrieves a role by name.
     *
     * @param name the role name
     * @return the role domain entity
     * @throws IllegalArgumentException if the role is not found
     */
    Role getByName(String name);
    
    /**
     * Retrieves all roles.
     *
     * @return a list of all roles
     */
    List<Role> getAll();
}
