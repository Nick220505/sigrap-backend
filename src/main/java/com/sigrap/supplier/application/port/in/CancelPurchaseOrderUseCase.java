package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;

/**
 * Input port for cancelling a purchase order.
 * This interface defines the use case for purchase order cancellation.
 */
public interface CancelPurchaseOrderUseCase {
    
    /**
     * Cancels a purchase order.
     *
     * @param id the purchase order identifier
     * @return the cancelled purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be cancelled
     */
    PurchaseOrder cancel(PurchaseOrderId id);
}
