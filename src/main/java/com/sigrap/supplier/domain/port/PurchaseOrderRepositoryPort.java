package com.sigrap.supplier.domain.port;

import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port (output port) for PurchaseOrder aggregate.
 * This interface is defined in the domain layer and expresses domain needs,
 * not database operations. It will be implemented by infrastructure adapters.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Defined in domain layer (no framework dependencies)</li>
 *   <li>Returns domain entities, not persistence entities</li>
 *   <li>Uses domain value objects for parameters</li>
 *   <li>Expresses business intent, not technical operations</li>
 * </ul>
 */
public interface PurchaseOrderRepositoryPort {

    /**
     * Saves a purchase order (create or update).
     * If the purchase order is new (no ID), it will be created.
     * If the purchase order has an ID, it will be updated.
     *
     * @param purchaseOrder the purchase order to save
     * @return the saved purchase order with generated ID if it was new
     */
    PurchaseOrder save(PurchaseOrder purchaseOrder);

    /**
     * Finds a purchase order by its identifier.
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
    List<PurchaseOrder> findAll();

    /**
     * Finds all purchase orders for a specific supplier.
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase orders for the supplier, empty list if none exist
     */
    List<PurchaseOrder> findBySupplierId(SupplierId supplierId);

    /**
     * Finds all purchase orders with a specific status.
     *
     * @param status the purchase order status
     * @return a list of purchase orders with the given status, empty list if none exist
     */
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    /**
     * Deletes a purchase order by its identifier.
     *
     * @param id the purchase order identifier
     */
    void deleteById(PurchaseOrderId id);
}
