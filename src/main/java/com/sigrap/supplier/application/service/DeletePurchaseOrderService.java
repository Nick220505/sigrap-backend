package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.DeletePurchaseOrderUseCase;
import com.sigrap.supplier.domain.model.PurchaseOrderId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeletePurchaseOrderUseCase.
 * This service handles purchase order deletion operations.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 * </ul>
 */
@Service
@Transactional
public class DeletePurchaseOrderService implements DeletePurchaseOrderUseCase {
    
    private final PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param purchaseOrderRepository the repository port for purchase order persistence
     */
    public DeletePurchaseOrderService(PurchaseOrderRepositoryPort purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }
    
    /**
     * Deletes a purchase order by its identifier.
     * Verifies the purchase order exists before deletion.
     *
     * @param id the purchase order identifier
     * @throws IllegalArgumentException if the purchase order is not found
     */
    @Override
    public void delete(PurchaseOrderId id) {
        // Verify purchase order exists
        if (purchaseOrderRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException(
                "Purchase order with ID " + id.value() + " not found"
            );
        }
        
        purchaseOrderRepository.deleteById(id);
    }
}
