package com.sigrap.sale.application.port.in;

import com.sigrap.sale.application.port.in.command.UpdateSaleItemCommand;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;

/**
 * Input port for updating a sale item.
 * This interface defines the use case for sale item updates.
 */
public interface UpdateSaleItemUseCase {
    
    /**
     * Updates an existing sale item with the provided command data.
     *
     * @param id the sale item identifier
     * @param command the command containing sale item update data
     * @return the updated sale item domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale item is not found
     * @throws IllegalStateException if the item cannot be updated
     */
    SaleItem updateItem(SaleItemId id, UpdateSaleItemCommand command);
}
