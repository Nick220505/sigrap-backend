package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.DeleteSaleUseCase;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeleteSaleUseCase.
 * This service handles deletion of sales.
 */
@Service
@Transactional
public class DeleteSaleService implements DeleteSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public DeleteSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Deletes a sale by its identifier.
     *
     * @param id the sale identifier
     * @throws ResourceNotFoundException if the sale is not found
     */
    @Override
    public void delete(SaleId id) {
        // Verify sale exists
        if (saleRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                "Sale not found with id: " + id.value()
            );
        }
        
        // Delete through port
        saleRepository.deleteById(id);
    }
}
