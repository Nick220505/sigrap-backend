package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.CompleteSaleReturnUseCase;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CompleteSaleReturnUseCase.
 * This service handles completing sale returns.
 */
@Service
@Transactional
public class CompleteSaleReturnService implements CompleteSaleReturnUseCase {
    
    private final SaleReturnRepositoryPort saleReturnRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleReturnRepository the repository port for sale return persistence
     */
    public CompleteSaleReturnService(SaleReturnRepositoryPort saleReturnRepository) {
        this.saleReturnRepository = saleReturnRepository;
    }
    
    /**
     * Completes a sale return, marking the refund as processed.
     *
     * @param id the sale return identifier
     * @return the completed sale return domain entity
     * @throws ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be completed
     */
    @Override
    public SaleReturn complete(SaleReturnId id) {
        // Retrieve existing sale return
        SaleReturn saleReturn = saleReturnRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale return not found with id: " + id.value()
            ));
        
        // Complete through domain method (enforces business rules)
        saleReturn.complete();
        
        // Persist changes
        return saleReturnRepository.save(saleReturn);
    }
}
