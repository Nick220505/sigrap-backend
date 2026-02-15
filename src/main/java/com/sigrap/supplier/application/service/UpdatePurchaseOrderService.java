package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.UpdatePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.command.UpdatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdatePurchaseOrderUseCase.
 * This service orchestrates the update of an existing purchase order.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Enforces business rules</li>
 * </ul>
 */
@Service
@Transactional
public class UpdatePurchaseOrderService implements UpdatePurchaseOrderUseCase {
    
    private final PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param purchaseOrderRepository the repository port for purchase order persistence
     */
    public UpdatePurchaseOrderService(PurchaseOrderRepositoryPort purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }
    
    /**
     * Updates an existing purchase order with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Purchase order must exist</li>
     *   <li>Purchase order must be in a modifiable state (enforced by domain entity)</li>
     * </ul>
     *
     * @param id the identifier of the purchase order to update
     * @param command the command containing updated purchase order data
     * @return the updated purchase order domain entity
     * @throws IllegalArgumentException if the purchase order is not found
     * @throws IllegalStateException if the purchase order cannot be modified
     */
    @Override
    public PurchaseOrder update(PurchaseOrderId id, UpdatePurchaseOrderCommand command) {
        // Retrieve existing purchase order
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Purchase order with ID " + id.value() + " not found"
            ));
        
        // Business rule: purchase order must be modifiable
        if (!purchaseOrder.canBeModified()) {
            throw new IllegalStateException(
                "Purchase order with status " + purchaseOrder.getStatus() + " cannot be modified"
            );
        }
        
        // Update domain entity using business methods
        purchaseOrder.updateExpectedDeliveryDate(command.expectedDeliveryDate());
        purchaseOrder.updateNotes(command.notes());
        
        // Persist through port
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
