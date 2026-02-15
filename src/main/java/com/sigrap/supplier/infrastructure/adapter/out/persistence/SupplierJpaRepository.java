package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for SupplierJpaEntity.
 * Provides database operations for supplier persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by SupplierPersistenceAdapter.
 */
@Repository
public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, Long> {
    
    /**
     * Checks if a supplier with the given email exists.
     *
     * @param email the supplier email to check
     * @return true if a supplier with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a supplier with the given email exists, excluding a specific supplier.
     * Used when updating a supplier to ensure email uniqueness.
     *
     * @param email the supplier email to check
     * @param id the supplier ID to exclude from the check
     * @return true if another supplier with this email exists, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
