package com.sigrap.sale.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for SaleItemJpaEntity.
 * Provides CRUD operations and custom query methods for sale item persistence.
 */
@Repository
public interface SaleItemJpaRepository extends JpaRepository<SaleItemJpaEntity, Long> {
    
    /**
     * Finds all sale items for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale item entities for the sale
     */
    List<SaleItemJpaEntity> findBySaleId(Long saleId);
}
