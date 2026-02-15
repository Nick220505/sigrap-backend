package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.GetSaleReturnUseCase;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnStatus;
import com.sigrap.sale.domain.port.SaleReturnRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetSaleReturnUseCase.
 * This service handles retrieval operations for sale returns.
 */
@Service
@Transactional(readOnly = true)
public class GetSaleReturnService implements GetSaleReturnUseCase {
    
    private final SaleReturnRepositoryPort saleReturnRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param saleReturnRepository the repository port for sale return persistence
     */
    public GetSaleReturnService(SaleReturnRepositoryPort saleReturnRepository) {
        this.saleReturnRepository = saleReturnRepository;
    }
    
    /**
     * Retrieves a sale return by its identifier.
     *
     * @param id the sale return identifier
     * @return the sale return domain entity
     * @throws ResourceNotFoundException if the sale return is not found
     */
    @Override
    public SaleReturn getById(SaleReturnId id) {
        return saleReturnRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Sale return not found with id: " + id.value()
            ));
    }
    
    /**
     * Retrieves a sale return by its identifier, returning an Optional.
     *
     * @param id the sale return identifier
     * @return an Optional containing the sale return if found, empty otherwise
     */
    @Override
    public Optional<SaleReturn> findById(SaleReturnId id) {
        return saleReturnRepository.findById(id);
    }
    
    /**
     * Retrieves all sale returns.
     *
     * @return a list of all sale return domain entities
     */
    @Override
    public List<SaleReturn> getAll() {
        return saleReturnRepository.findAll();
    }
    
    /**
     * Retrieves all sale returns for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale returns for the sale
     */
    @Override
    public List<SaleReturn> getBySaleId(SaleId saleId) {
        return saleReturnRepository.findBySaleId(saleId);
    }
    
    /**
     * Retrieves all sale returns with a specific status.
     *
     * @param status the sale return status
     * @return a list of sale returns with the given status
     */
    @Override
    public List<SaleReturn> getByStatus(SaleReturnStatus status) {
        return saleReturnRepository.findByStatus(status);
    }
}
