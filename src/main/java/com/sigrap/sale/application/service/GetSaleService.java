package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.GetSaleUseCase;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleStatus;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetSaleUseCase.
 * This service handles retrieval operations for sales.
 */
@Service
@Transactional(readOnly = true)
public class GetSaleService implements GetSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleRepository the repository port for sale persistence
     */
    public GetSaleService(SaleRepositoryPort saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Retrieves a sale by its identifier.
     *
     * @param id the sale identifier
     * @return the sale domain entity
     * @throws ResourceNotFoundException if the sale is not found
     */
    @Override
    public Sale getById(SaleId id) {
        return saleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale not found with id: " + id.value()
            ));
    }
    
    /**
     * Retrieves a sale by its identifier, returning an Optional.
     *
     * @param id the sale identifier
     * @return an Optional containing the sale if found, empty otherwise
     */
    @Override
    public Optional<Sale> findById(SaleId id) {
        return saleRepository.findById(id);
    }
    
    /**
     * Retrieves all sales.
     *
     * @return a list of all sale domain entities
     */
    @Override
    public List<Sale> getAll() {
        return saleRepository.findAll();
    }
    
    /**
     * Retrieves all sales for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of sales for the customer
     */
    @Override
    public List<Sale> getByCustomerId(Long customerId) {
        return saleRepository.findByCustomerId(customerId);
    }
    
    /**
     * Retrieves all sales with a specific status.
     *
     * @param status the sale status
     * @return a list of sales with the given status
     */
    @Override
    public List<Sale> getByStatus(SaleStatus status) {
        return saleRepository.findByStatus(status);
    }
}
