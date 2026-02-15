package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleStatus;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving sales.
 * This interface defines the use cases for sale retrieval operations.
 */
public interface GetSaleUseCase {
    
    /**
     * Retrieves a sale by its identifier.
     *
     * @param id the sale identifier
     * @return the sale domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    Sale getById(SaleId id);
    
    /**
     * Retrieves a sale by its identifier, returning an Optional.
     *
     * @param id the sale identifier
     * @return an Optional containing the sale if found, empty otherwise
     */
    Optional<Sale> findById(SaleId id);
    
    /**
     * Retrieves all sales.
     *
     * @return a list of all sale domain entities
     */
    List<Sale> getAll();
    
    /**
     * Retrieves all sales for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of sales for the customer
     */
    List<Sale> getByCustomerId(Long customerId);
    
    /**
     * Retrieves all sales with a specific status.
     *
     * @param status the sale status
     * @return a list of sales with the given status
     */
    List<Sale> getByStatus(SaleStatus status);
}
