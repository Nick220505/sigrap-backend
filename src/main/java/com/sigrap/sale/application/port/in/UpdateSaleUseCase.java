package com.sigrap.sale.application.port.in;

import com.sigrap.sale.application.port.in.command.UpdateSaleCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;

/**
 * Input port for updating an existing sale.
 * This interface defines the use case for sale updates.
 */
public interface UpdateSaleUseCase {
    
    /**
     * Updates an existing sale with the provided command data.
     *
     * @param id the sale identifier
     * @param command the command containing sale update data
     * @return the updated sale domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be modified
     */
    Sale update(SaleId id, UpdateSaleCommand command);
}
