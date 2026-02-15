package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.AddSaleItemUseCase;
import com.sigrap.sale.application.port.in.command.AddSaleItemCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the AddSaleItemUseCase.
 * This service handles adding items to sales.
 */
@Service
@Transactional
public class AddSaleItemService implements AddSaleItemUseCase {
    
    private final SaleItemRepositoryPort saleItemRepository;
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleItemRepository the repository port for sale item persistence
     * @param saleRepository the repository port for sale persistence
     */
    public AddSaleItemService(SaleItemRepositoryPort saleItemRepository,
                             SaleRepositoryPort saleRepository) {
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
    }
    
    /**
     * Adds a new item to a sale with the provided command data.
     *
     * @param command the command containing sale item data
     * @return the created sale item domain entity
     * @throws ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if items cannot be added to the sale
     */
    @Override
    public SaleItem addItem(AddSaleItemCommand command) {
        // Verify sale exists and can be modified
        SaleId saleId = new SaleId(command.saleId());
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + command.saleId()
            ));
        
        // Create sale item domain entity
        SaleItem saleItem = new SaleItem(
            saleId,
            command.productId(),
            command.quantity(),
            command.unitPrice()
        );
        
        // Add item to sale (enforces business rules)
        sale.addItem(saleItem);
        
        // Persist sale item
        SaleItem savedItem = saleItemRepository.save(saleItem);
        
        // Update sale with recalculated total
        saleRepository.save(sale);
        
        return savedItem;
    }
}
