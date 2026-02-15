package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;

/**
 * Input port for completing a sale return.
 * This interface defines the use case for completing sale returns.
 */
public interface CompleteSaleReturnUseCase {
    
    /**
     * Completes a sale return, marking the refund as processed.
     *
     * @param id the sale return identifier
     * @return the completed sale return domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be completed
     */
    SaleReturn complete(SaleReturnId id);
}
