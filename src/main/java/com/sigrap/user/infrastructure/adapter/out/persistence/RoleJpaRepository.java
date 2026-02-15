package com.sigrap.user.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for RoleJpaEntity.
 * Provides CRUD operations and custom query methods for role persistence.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {
    
    /**
     * Finds a role by name.
     *
     * @param name the role name to search for
     * @return an Optional containing the role if found
     */
    Optional<RoleJpaEntity> findByName(String name);
    
    /**
     * Checks if a role with the given name exists.
     *
     * @param name the role name to check
     * @return true if a role with this name exists
     */
    boolean existsByName(String name);
}
