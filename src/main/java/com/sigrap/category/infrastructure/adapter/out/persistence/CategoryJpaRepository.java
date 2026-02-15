package com.sigrap.category.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for CategoryJpaEntity.
 * Provides database operations for category persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by CategoryPersistenceAdapter.
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
    
    /**
     * Checks if a category with the given name exists.
     *
     * @param name the category name to check
     * @return true if a category with the name exists, false otherwise
     */
    boolean existsByName(String name);
}
