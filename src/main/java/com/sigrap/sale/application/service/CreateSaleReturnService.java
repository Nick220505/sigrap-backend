package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.CreateSaleReturnUseCase;
import com.sigrap.sale.application.port.in.command.CreateSaleReturnCommand;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnNumber;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateSaleReturnUseCase.
 * This service orchestrates the creation of a new sale return.
 */
@Service
@Transactional
public class CreateSaleReturnService implements CreateSaleReturnUseCase {
    
    private final SaleReturnRepositoryPort saleReturnRepository;
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleReturnRepository the repository port for sale return persistence
     * @param saleRepository the repository port for sale persistence
     */
    public CreateSaleReturnService(SaleReturnRepositoryPort saleReturnRepository,
                                  SaleRepositoryPort saleRepository) {
        this.saleReturnRepository = saleReturnRepository;
        this.saleRepository = saleRepository;
    }
    
    /**
     * Creates a new sale return with the provided command data.
     *
     * @param command the command containing sale return creation data
     * @return the created sale return domain entity with generated ID
     * @throws ResourceNotFoundException if the sale is not found
     */
    @Override
    public SaleReturn create(CreateSaleReturnCommand command) {
        // Verify sale exists
        SaleId saleId = new SaleId(command.saleId());
        if (saleRepository.findById(saleId).isEmpty()) {
            throw new ResourceNotFoundException(
                "Sale not found with id: " + command.saleId()
            );
        }
        
        // Create value objects
        SaleReturnNumber returnNumber = new SaleReturnNumber(command.returnNumber());
        
        // Create domain entity
        SaleReturn saleReturn = new SaleReturn(
            returnNumber,
            saleId,
            command.returnDate(),
            command.reason(),
            command.refundAmount(),
            command.notes()
        );
        
        // Persist through port and return with generated ID
        return saleReturnRepository.save(saleReturn);
    }
}
