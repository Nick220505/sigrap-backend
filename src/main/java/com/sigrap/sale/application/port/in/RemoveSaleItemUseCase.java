package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleItemId;

/**
 * Input port for removing an item from a sale.
 * This interface defines the use case for removing sale items.
 */
public interface RemoveSaleItemUseCase {
    
    /**
     * Removes an item from a sale.
     *
     * @param id the sale item identifier
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale item is not found
     * @throws IllegalStateException if the item cannot be removed
     */
    void removeItem(SaleItemId id);
}
