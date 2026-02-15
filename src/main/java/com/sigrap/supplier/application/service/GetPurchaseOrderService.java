package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.GetPurchaseOrderUseCase;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.model.PurchaseOrderStatus;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetPurchaseOrderUseCase.
 * This service handles purchase order retrieval operations.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for data access</li>
 *   <li>Read-only operations use @Transactional(readOnly = true)</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class GetPurchaseOrderService implements GetPurchaseOrderUseCase {
    
    private final PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param purchaseOrderRepository the repository port for purchase order data access
     */
    public GetPurchaseOrderService(PurchaseOrderRepositoryPort purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }
    
    /**
     * Retrieves a purchase order by its identifier.
     * Throws an exception if the purchase order is not found.
     *
     * @param id the purchase order identifier
     * @return the purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     */
    @Override
    public PurchaseOrder getById(PurchaseOrderId id) {
        return purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Purchase order with ID " + id.value() + " not found"
            ));
    }
    
    /**
     * Finds a purchase order by its identifier.
     * Returns an empty Optional if the purchase order is not found.
     *
     * @param id the purchase order identifier
     * @return an Optional containing the purchase order if found, empty otherwise
     */
    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        return purchaseOrderRepository.findById(id);
    }
    
    /**
     * Retrieves all purchase orders.
     *
     * @return a list of all purchase orders, empty list if none exist
     */
    @Override
    public List<PurchaseOrder> getAll() {
        return purchaseOrderRepository.findAll();
    }
    
    /**
     * Retrieves all purchase orders for a specific supplier.
     *
     * @param supplierId the supplier identifier
     * @return a list of purchase orders for the supplier, empty list if none exist
     */
    @Override
    public List<PurchaseOrder> getBySupplierId(SupplierId supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }
    
    /**
     * Retrieves all purchase orders with a specific status.
     *
     * @param status the purchase order status
     * @return a list of purchase orders with the given status, empty list if none exist
     */
    @Override
    public List<PurchaseOrder> getByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }
}
