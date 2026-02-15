package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.CreatePurchaseOrderUseCase;
import com.sigrap.supplier.application.port.in.command.CreatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.PurchaseOrder;
import com.sigrap.supplier.domain.model.PurchaseOrderNumber;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreatePurchaseOrderUseCase.
 * This service orchestrates the creation of a new purchase order by:
 * <ul>
 *   <li>Validating business rules (e.g., supplier exists)</li>
 *   <li>Creating the domain entity</li>
 *   <li>Persisting through the repository port</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output ports (repositories) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class CreatePurchaseOrderService implements CreatePurchaseOrderUseCase {
    
    private final PurchaseOrderRepositoryPort purchaseOrderRepository;
    private final SupplierRepositoryPort supplierRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param purchaseOrderRepository the repository port for purchase order persistence
     * @param supplierRepository the repository port for supplier data access
     */
    public CreatePurchaseOrderService(
            PurchaseOrderRepositoryPort purchaseOrderRepository,
            SupplierRepositoryPort supplierRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Creates a new purchase order with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Supplier must exist</li>
     *   <li>Order number must be valid (enforced by PurchaseOrderNumber value object)</li>
     *   <li>Total amount must be non-negative (enforced by PurchaseOrder entity)</li>
     * </ul>
     *
     * @param command the command containing purchase order creation data
     * @return the created purchase order domain entity with generated ID
     * @throws IllegalArgumentException if the supplier does not exist
     * @throws IllegalArgumentException if the order number or total amount is invalid
     */
    @Override
    public PurchaseOrder create(CreatePurchaseOrderCommand command) {
        // Create value objects (validates format)
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber(command.orderNumber());
        SupplierId supplierId = new SupplierId(command.supplierId());
        
        // Business rule: supplier must exist
        if (supplierRepository.findById(supplierId).isEmpty()) {
            throw new IllegalArgumentException(
                "Supplier with ID " + supplierId.value() + " not found"
            );
        }
        
        // Create domain entity
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            orderNumber,
            supplierId,
            command.orderDate(),
            command.expectedDeliveryDate(),
            command.totalAmount(),
            command.notes()
        );
        
        // Persist through port and return with generated ID
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
