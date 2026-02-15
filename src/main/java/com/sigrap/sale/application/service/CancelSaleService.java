package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.CancelSaleUseCase;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CancelSaleUseCase.
 * This service handles cancelling sales.
 */
@Service
@Transactional
public class CancelSaleService implements CancelSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public CancelSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Cancels a sale.
     *
     * @param id the sale identifier
     * @return the cancelled sale domain entity
     * @throws ResourceNotFoundException if the sale is not found
     * @throws IllegalStateException if the sale cannot be cancelled
     */
    @Override
    public Sale cancel(SaleId id) {
        // Retrieve existing sale
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + id.value()
            ));
        
        // Cancel through domain method (enforces business rules)
        sale.cancel();
        
        // Persist changes
        return saleRepository.save(sale);
    }
}
