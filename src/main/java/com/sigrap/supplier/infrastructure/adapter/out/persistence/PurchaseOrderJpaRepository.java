package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository interface for PurchaseOrderJpaEntity.
 * Provides database operations for purchase order persistence in the hexagonal architecture.
 * This is an output adapter component that will be used by PurchaseOrderPersistenceAdapter.
 */
@Repository
public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderJpaEntity, Long> {
    
    /**
     * Finds all purchase orders for a specific supplier.
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase orders for the supplier
     */
    List<PurchaseOrderJpaEntity> findBySupplierId(Long supplierId);
    
    /**
     * Finds all purchase orders with a specific status.
     *
     * @param status the purchase order status
     * @return a list of purchase orders with the given status
     */
    List<PurchaseOrderJpaEntity> findByStatus(PurchaseOrderJpaEntity.PurchaseOrderStatusJpa status);
}
