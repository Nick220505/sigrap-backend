package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.PurchaseOrderId;

/**
 * Input port for deleting purchase orders.
 * This interface defines the use case for purchase order deletion operations.
 */
public interface DeletePurchaseOrderUseCase {
    
    /**
     * Deletes a purchase order by its identifier.
     *
     * @param id the purchase order identifier
     * @throws IllegalArgumentException if the purchase order is not found
     */
    void delete(PurchaseOrderId id);
}
