package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.application.port.in.command.CreatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.PurchaseOrder;

/**
 * Input port for creating a new purchase order.
 * This interface defines the use case for purchase order creation.
 */
public interface CreatePurchaseOrderUseCase {
    
    /**
     * Creates a new purchase order with the provided command data.
     *
     * @param command the command containing purchase order creation data
     * @return the created purchase order domain entity
     * @throws IllegalArgumentException if the supplier does not exist
     */
    PurchaseOrder create(CreatePurchaseOrderCommand command);
}
