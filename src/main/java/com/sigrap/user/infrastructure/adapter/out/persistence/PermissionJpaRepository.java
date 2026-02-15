package com.sigrap.user.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for PermissionJpaEntity.
 * Provides CRUD operations and custom query methods for permission persistence.
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {
    
    /**
     * Finds a permission by name.
     *
     * @param name the permission name to search for
     * @return an Optional containing the permission if found
     */
    Optional<PermissionJpaEntity> findByName(String name);
    
    /**
     * Checks if a permission with the given name exists.
     *
     * @param name the permission name to check
     * @return true if a permission with this name exists
     */
    boolean existsByName(String name);
    
    /**
     * Finds all permissions for a specific resource.
     *
     * @param resource the resource name
     * @return a list of permissions for the resource
     */
    List<PermissionJpaEntity> findByResource(String resource);
}
