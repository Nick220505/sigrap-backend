package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.RemoveSaleItemUseCase;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RemoveSaleItemUseCase.
 * This service handles removing items from sales.
 */
@Service
@Transactional
public class RemoveSaleItemService implements RemoveSaleItemUseCase {
    
    private final SaleItemRepositoryPort saleItemRepository;
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleItemRepository the repository port for sale item persistence
     * @param saleRepository the repository port for sale persistence
     */
    public RemoveSaleItemService(SaleItemRepositoryPort saleItemRepository,
                                SaleRepositoryPort saleRepository) {
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
    }
    
    /**
     * Removes an item from a sale.
     *
     * @param id the sale item identifier
     * @throws ResourceNotFoundException if the sale item is not found
     * @throws IllegalStateException if the item cannot be removed
     */
    @Override
    public void removeItem(SaleItemId id) {
        // Retrieve sale item
        SaleItem saleItem = saleItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale item not found with id: " + id.value()
            ));
        
        // Retrieve sale
        Sale sale = saleRepository.findById(saleItem.getSaleId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + saleItem.getSaleId().value()
            ));
        
        // Remove item from sale (enforces business rules)
        sale.removeItem(saleItem);
        
        // Delete sale item
        saleItemRepository.deleteById(id);
        
        // Update sale with recalculated total
        saleRepository.save(sale);
    }
}
