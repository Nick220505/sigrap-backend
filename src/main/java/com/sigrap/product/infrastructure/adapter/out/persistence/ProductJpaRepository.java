package com.sigrap.product.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository interface for ProductJpaEntity.
 * Provides database operations for product persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by ProductPersistenceAdapter.
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    
    /**
     * Checks if a product with the given name exists.
     *
     * @param name the product name to check
     * @return true if a product with the name exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Checks if a product with the given name exists, excluding a specific product ID.
     * Useful for update operations to check name uniqueness.
     *
     * @param name the product name to check
     * @param id the product ID to exclude from the check
     * @return true if another product with the name exists, false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * Finds all products belonging to a specific category.
     *
     * @param categoryId the category identifier
     * @return a list of products in the category
     */
    List<ProductJpaEntity> findByCategoryId(Long categoryId);
}
