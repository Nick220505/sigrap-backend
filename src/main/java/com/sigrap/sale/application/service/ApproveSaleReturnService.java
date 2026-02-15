package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.ApproveSaleReturnUseCase;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the ApproveSaleReturnUseCase.
 * This service handles approving sale returns.
 */
@Service
@Transactional
public class ApproveSaleReturnService implements ApproveSaleReturnUseCase {
    
    private final SaleReturnRepositoryPort saleReturnRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleReturnRepository the repository port for sale return persistence
     */
    public ApproveSaleReturnService(SaleReturnRepositoryPort saleReturnRepository) {
        this.saleReturnRepository = saleReturnRepository;
    }
    
    /**
     * Approves a sale return.
     *
     * @param id the sale return identifier
     * @return the approved sale return domain entity
     * @throws ResourceNotFoundException if the sale return is not found
     * @throws IllegalStateException if the sale return cannot be approved
     */
    @Override
    public SaleReturn approve(SaleReturnId id) {
        // Retrieve existing sale return
        SaleReturn saleReturn = saleReturnRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale return not found with id: " + id.value()
            ));
        
        // Approve through domain method (enforces business rules)
        saleReturn.approve();
        
        // Persist changes
        return saleReturnRepository.save(saleReturn);
    }
}
