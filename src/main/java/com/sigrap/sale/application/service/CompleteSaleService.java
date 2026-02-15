package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.CompleteSaleUseCase;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CompleteSaleUseCase.
 * This service handles completing sales.
 */
@Service
@Transactional
public class CompleteSaleService implements CompleteSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public CompleteSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Completes a sale, marking it as finalized.
     *
     * @param id the sale identifier
     * @return the completed sale domain entity
     * @throws ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be completed
     */
    @Override
    public Sale complete(SaleId id) {
        // Retrieve existing sale
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + id.value()
            ));
        
        // Complete through domain method (enforces business rules)
        sale.complete();
        
        // Persist changes
        return saleRepository.save(sale);
    }
}
