package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving purchase orders.
 * This interface defines the use cases for purchase order retrieval operations.
 */
public interface GetPurchaseOrderUseCase {
    
    /**
     * Retrieves a purchase order by its identifier.
     * Throws an exception if the purchase order is not found.
     *
     * @param id the purchase order identifier
     * @return the purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     */
    PurchaseOrder getById(PurchaseOrderId id);
    
    /**
     * Finds a purchase order by its identifier.
     * Returns an empty Optional if the purchase order is not found.
     *
     * @param id the purchase order identifier
     * @return an Optional containing the purchase order if found, empty otherwise
     */
    Optional<PurchaseOrder> findById(PurchaseOrderId id);
    
    /**
     * Retrieves all purchase orders.
     *
     * @return a list of all purchase orders, empty list if none exist
     */
    List<PurchaseOrder> getAll();
    
    /**
     * Retrieves all purchase orders for a specific supplier.
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase orders for the supplier, empty list if none exist
     */
    List<PurchaseOrder> getBySupplierId(SupplierId supplierId);
    
    /**
     * Retrieves all purchase orders with a specific status.
     *
     * @param status the purchase order status
     * @return a list of purchase orders with the given status, empty list if none exist
     */
    List<PurchaseOrder> getByStatus(PurchaseOrderStatus status);
}
