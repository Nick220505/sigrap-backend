package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;

/**
 * Input port for cancelling a sale.
 * This interface defines the use case for cancelling a sale.
 */
public interface CancelSaleUseCase {
    
    /**
     * Cancels a sale.
     *
     * @param id the sale identifier
     * @return the cancelled sale domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be cancelled
     */
    Sale cancel(SaleId id);
}
