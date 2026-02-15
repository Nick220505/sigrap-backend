package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.UpdateSaleItemUseCase;
import com.sigrap.sale.application.port.in.command.UpdateSaleItemCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateSaleItemUseCase.
 * This service handles updating sale items.
 */
@Service
@Transactional
public class UpdateSaleItemService implements UpdateSaleItemUseCase {
    
    private final SaleItemRepositoryPort saleItemRepository;
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleItemRepository the repository port for sale item persistence
     * @param saleRepository the repository port for sale persistence
     */
    public UpdateSaleItemService(SaleItemRepositoryPort saleItemRepository,
                                SaleRepositoryPort saleRepository) {
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
    }
    
    /**
     * Updates an existing sale item with the provided command data.
     *
     * @param id the sale item identifier
     * @param command the command containing sale item update data
     * @return the updated sale item domain entity
     * @throws ResourceNotFoundException if the sale item is not found
     * @throws IllegalStateException if the item cannot be updated
     */
    @Override
    public SaleItem updateItem(SaleItemId id, UpdateSaleItemCommand command) {
        // Retrieve sale item
        SaleItem saleItem = saleItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale item not found with id: " + id.value()
            ));
        
        // Retrieve sale to verify it can be modified
        Sale sale = saleRepository.findById(saleItem.getSaleId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + saleItem.getSaleId().value()
            ));
        
        // Verify sale can be modified
        if (!sale.canBeModified()) {
            throw new IllegalStateException(
                "Cannot update items of a sale with status: " + sale.getStatus()
            );
        }
        
        // Update fields through domain methods
        if (command.quantity() != null) {
            saleItem.updateQuantity(command.quantity());
        }
        
        if (command.unitPrice() != null) {
            saleItem.updateUnitPrice(command.unitPrice());
        }
        
        // Persist sale item
        SaleItem updatedItem = saleItemRepository.save(saleItem);
        
        // Recalculate and update sale total
        sale.calculateTotal();
        saleRepository.save(sale);
        
        return updatedItem;
    }
}
