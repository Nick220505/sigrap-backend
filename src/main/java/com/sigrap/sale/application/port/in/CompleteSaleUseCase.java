package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;

/**
 * Input port for completing a sale.
 * This interface defines the use case for marking a sale as completed.
 */
public interface CompleteSaleUseCase {
    
    /**
     * Completes a sale, marking it as finalized.
     *
     * @param id the sale identifier
     * @return the completed sale domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be completed
     */
    Sale complete(SaleId id);
}
