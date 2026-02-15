package com.sigrap.customer.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for CustomerJpaEntity.
 * Provides database operations for customer persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by CustomerPersistenceAdapter.
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
    
    /**
     * Checks if a customer with the given email exists.
     *
     * @param email the customer email to check
     * @return true if a customer with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a customer with the given email exists, excluding a specific customer ID.
     * Useful for update operations to check email uniqueness.
     *
     * @param email the customer email to check
     * @param id the customer ID to exclude from the check
     * @return true if another customer with the email exists, false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
