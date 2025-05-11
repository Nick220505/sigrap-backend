package com.sigrap.permission;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Permission entity operations.
 * Provides methods for permission-specific database operations beyond basic CRUD.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  /**
   * Finds a permission by its name.
   *
   * @param name The name of the permission to search for
   * @return Optional containing the permission if found, empty otherwise
   */
  Optional<Permission> findByName(String name);

  /**
   * Checks if a permission exists with the given name.
   *
   * @param name The name to check for
   * @return true if a permission exists with the name, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Finds all permissions related to a specific resource.
   *
   * @param resource The resource to search for
   * @return List of permissions for the resource
   */
  List<Permission> findByResource(String resource);

  /**
   * Finds a permission by its resource and action.
   *
   * @param resource The resource to search for
   * @param action The action to search for
   * @return Optional containing the permission if found, empty otherwise
   */
  Optional<Permission> findByResourceAndAction(String resource, String action);
}
