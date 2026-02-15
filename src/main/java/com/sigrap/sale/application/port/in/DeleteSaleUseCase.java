package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleId;

/**
 * Input port for deleting a sale.
 * This interface defines the use case for sale deletion.
 */
public interface DeleteSaleUseCase {
    
    /**
     * Deletes a sale by its identifier.
     *
     * @param id the sale identifier
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    void delete(SaleId id);
}
