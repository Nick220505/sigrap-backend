package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;

/**
 * Input port for approving a purchase order.
 * This interface defines the use case for purchase order approval.
 */
public interface ApprovePurchaseOrderUseCase {
    
    /**
     * Approves a purchase order.
     *
     * @param id the purchase order identifier
     * @return the approved purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be approved
     */
    PurchaseOrder approve(PurchaseOrderId id);
}
