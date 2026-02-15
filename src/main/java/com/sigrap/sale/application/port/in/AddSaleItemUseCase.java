package com.sigrap.sale.application.port.in;

import com.sigrap.sale.application.port.in.command.AddSaleItemCommand;
import com.sigrap.sale.domain.model.SaleItem;

/**
 * Input port for adding an item to a sale.
 * This interface defines the use case for adding sale items.
 */
public interface AddSaleItemUseCase {
    
    /**
     * Adds a new item to a sale with the provided command data.
     *
     * @param command the command containing sale item data
     * @return the created sale item domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if items cannot be added to the sale
     */
    SaleItem addItem(AddSaleItemCommand command);
}
