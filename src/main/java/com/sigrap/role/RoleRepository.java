package com.sigrap.role;

import com.sigrap.permission.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Role entity operations.
 * Provides methods for role-specific database operations beyond basic CRUD.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  /**
   * Finds a role by its name.
   *
   * @param name The name of the role to search for
   * @return Optional containing the role if found, empty otherwise
   */
  Optional<Role> findByName(String name);

  /**
   * Checks if a role exists with the given name.
   *
   * @param name The name to check for
   * @return true if a role exists with the name, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Finds all roles that contain a specific permission.
   *
   * @param permission The permission to search for in roles
   * @return List of roles containing the permission
   */
  List<Role> findByPermissionsContaining(Permission permission);
}
