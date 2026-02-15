package com.sigrap.sale.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for SaleJpaEntity.
 * Provides CRUD operations and custom query methods for sale persistence.
 */
@Repository
public interface SaleJpaRepository extends JpaRepository<SaleJpaEntity, Long> {
    
    /**
     * Finds all sales for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of sale entities for the customer
     */
    List<SaleJpaEntity> findByCustomerId(Long customerId);
    
    /**
     * Finds all sales with a specific status.
     *
     * @param status the sale status
     * @return a list of sale entities with the given status
     */
    List<SaleJpaEntity> findByStatus(String status);
}
