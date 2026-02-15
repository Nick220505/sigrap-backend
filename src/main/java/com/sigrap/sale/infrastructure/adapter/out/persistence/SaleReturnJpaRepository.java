package com.sigrap.sale.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for SaleReturnJpaEntity.
 * Provides CRUD operations and custom query methods for sale return persistence.
 */
@Repository
public interface SaleReturnJpaRepository extends JpaRepository<SaleReturnJpaEntity, Long> {
    
    /**
     * Finds all sale returns for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale return entities for the sale
     */
    List<SaleReturnJpaEntity> findBySaleId(Long saleId);
    
    /**
     * Finds all sale returns with a specific status.
     *
     * @param status the sale return status
     * @return a list of sale return entities with the given status
     */
    List<SaleReturnJpaEntity> findByStatus(String status);
}
