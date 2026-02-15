package com.sigrap.sale.domain.port;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for SaleItem domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface SaleItemRepositoryPort {
    
    /**
     * Saves a sale item (create or update).
     *
     * @param saleItem the sale item to save
     * @return the saved sale item with generated ID if new
     */
    SaleItem save(SaleItem saleItem);
    
    /**
     * Finds a sale item by its identifier.
     *
     * @param id the sale item identifier
     * @return an Optional containing the sale item if found, empty otherwise
     */
    Optional<SaleItem> findById(SaleItemId id);
    
    /**
     * Finds all sale items for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale items for the sale
     */
    List<SaleItem> findBySaleId(SaleId saleId);
    
    /**
     * Deletes a sale item by its identifier.
     *
     * @param id the sale item identifier
     */
    void deleteById(SaleItemId id);
}
