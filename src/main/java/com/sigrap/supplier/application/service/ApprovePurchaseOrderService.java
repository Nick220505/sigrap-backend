package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.ApprovePurchaseOrderUseCase;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the ApprovePurchaseOrderUseCase.
 * This service handles the approval of purchase orders.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Delegates business logic to domain entity</li>
 * </ul>
 */
@Service
@Transactional
public class ApprovePurchaseOrderService implements ApprovePurchaseOrderUseCase {
    
    private final PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param purchaseOrderRepository the repository port for purchase order persistence
     */
    public ApprovePurchaseOrderService(PurchaseOrderRepositoryPort purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }
    
    /**
     * Approves a purchase order.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Purchase order must exist</li>
     *   <li>Purchase order must be in PENDING status (enforced by domain entity)</li>
     * </ul>
     *
     * @param id the purchase order identifier
     * @return the approved purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be approved
     */
    @Override
    public PurchaseOrder approve(PurchaseOrderId id) {
        // Retrieve existing purchase order
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Purchase order with ID " + id.value() + " not found"
            ));
        
        // Delegate to domain entity (enforces business rules)
        purchaseOrder.approve();
        
        // Persist through port
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
