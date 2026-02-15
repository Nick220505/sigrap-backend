package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;

/**
 * Input port for receiving a purchase order.
 * This interface defines the use case for marking a purchase order as received.
 */
public interface ReceivePurchaseOrderUseCase {
    
    /**
     * Marks a purchase order as received.
     *
     * @param id the purchase order identifier
     * @return the received purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be received
     */
    PurchaseOrder receive(PurchaseOrderId id);
}
