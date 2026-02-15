package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.UpdateSaleUseCase;
import com.sigrap.sale.application.port.in.command.UpdateSaleCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateSaleUseCase.
 * This service handles updating existing sales.
 */
@Service
@Transactional
public class UpdateSaleService implements UpdateSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public UpdateSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Updates an existing sale with the provided command data.
     *
     * @param id the sale identifier
     * @param command the command containing sale update data
     * @return the updated sale domain entity
     * @throws ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be modified
     */
    @Override
    public Sale update(SaleId id, UpdateSaleCommand command) {
        // Retrieve existing sale
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + id.value()
            ));
        
        // Update fields through domain methods
        if (command.paymentMethod() != null) {
            sale.updatePaymentMethod(command.paymentMethod());
        }
        
        if (command.notes() != null) {
            sale.updateNotes(command.notes());
        }
        
        // Persist changes
        return saleRepository.save(sale);
    }
}
