package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;

/**
 * Input port for rejecting a sale return.
 * This interface defines the use case for rejecting sale returns.
 */
public interface RejectSaleReturnUseCase {
    
    /**
     * Rejects a sale return.
     *
     * @param id the sale return identifier
     * @return the rejected sale return domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be rejected
     */
    SaleReturn reject(SaleReturnId id);
}
