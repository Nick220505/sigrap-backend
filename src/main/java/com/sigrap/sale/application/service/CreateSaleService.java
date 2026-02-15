package com.sigrap.sale.application.service;

import com.sigrap.sale.application.port.in.CreateSaleUseCase;
import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleNumber;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateSaleUseCase.
 * This service orchestrates the creation of a new sale.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class CreateSaleService implements CreateSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public CreateSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Creates a new sale with the provided command data.
     *
     * @param command the command containing sale creation data
     * @return the created sale domain entity with generated ID
     */
    @Override
    public Sale create(CreateSaleCommand command) {
        // Create value objects
        SaleNumber saleNumber = new SaleNumber(command.saleNumber());
        
        // Create domain entity
        Sale sale = new Sale(
            saleNumber,
            command.customerId(),
            command.employeeId(),
            command.saleDate(),
            command.paymentMethod(),
            command.notes()
        );
        
        // Persist through port and return with generated ID
        return saleRepository.save(sale);
    }
}
