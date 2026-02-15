package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.RejectSaleReturnUseCase;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RejectSaleReturnUseCase.
 * This service handles rejecting sale returns.
 */
@Service
@Transactional
public class RejectSaleReturnService implements RejectSaleReturnUseCase {
    
    private final SaleReturnRepositoryPort saleReturnRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleReturnRepository the repository port for sale return persistence
     */
    public RejectSaleReturnService(SaleReturnRepositoryPort saleReturnRepository) {
        this.saleReturnRepository = saleReturnRepository;
    }
    
    /**
     * Rejects a sale return.
     *
     * @param id the sale return identifier
     * @return the rejected sale return domain entity
     * @throws ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be rejected
     */
    @Override
    public SaleReturn reject(SaleReturnId id) {
        // Retrieve existing sale return
        SaleReturn saleReturn = saleReturnRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale return not found with id: " + id.value()
            ));
        
        // Reject through domain method (enforces business rules)
        saleReturn.reject();
        
        // Persist changes
        return saleReturnRepository.save(saleReturn);
    }
}
