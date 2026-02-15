package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.application.port.in.command.UpdatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;

/**
 * Input port for updating an existing purchase order.
 * This interface defines the use case for purchase order updates.
 */
public interface UpdatePurchaseOrderUseCase {
    
    /**
     * Updates an existing purchase order with the provided command data.
     *
     * @param id the identifier of the purchase order to update
     * @param command the command containing updated purchase order data
     * @return the updated purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be modified
     */
    PurchaseOrder update(PurchaseOrderId id, UpdatePurchaseOrderCommand command);
}
