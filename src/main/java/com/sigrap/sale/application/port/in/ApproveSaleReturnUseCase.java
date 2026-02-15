package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;

/**
 * Input port for approving a sale return.
 * This interface defines the use case for approving sale returns.
 */
public interface ApproveSaleReturnUseCase {
    
    /**
     * Approves a sale return.
     *
     * @param id the sale return identifier
     * @return the approved sale return domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be approved
     */
    SaleReturn approve(SaleReturnId id);
}
