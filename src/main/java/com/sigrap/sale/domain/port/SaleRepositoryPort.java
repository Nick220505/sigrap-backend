package com.sigrap.sale.domain.port;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleStatus;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Sale domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface SaleRepositoryPort {
    
    /**
     * Saves a sale (create or update).
     *
     * @param sale the sale to save
     * @return the saved sale with generated ID if new
     */
    Sale save(Sale sale);
    
    /**
     * Finds a sale by its identifier.
     *
     * @param id the sale identifier
     * @return an Optional containing the sale if found, empty otherwise
     */
    Optional<Sale> findById(SaleId id);
    
    /**
     * Retrieves all sales.
     *
     * @return a list of all sales
     */
    List<Sale> findAll();
    
    /**
     * Finds all sales for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of sales for the customer
     */
    List<Sale> findByCustomerId(Long customerId);
    
    /**
     * Finds all sales with a specific status.
     *
     * @param status the sale status
     * @return a list of sales with the given status
     */
    List<Sale> findByStatus(SaleStatus status);
    
    /**
     * Deletes a sale by its identifier.
     *
     * @param id the sale identifier
     */
    void deleteById(SaleId id);


    /**
     * Counts the total number of sales.
     *
     * @return the total count of sales
     */
    long count();

    /**
     * Saves multiple sales at once.
     * Useful for batch operations.
     *
     * @param sales the list of sales to save
     * @return the list of saved sales
     */
    List<Sale> saveAll(List<Sale> sales);

}
