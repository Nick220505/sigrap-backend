package com.sigrap.sale.domain.port;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnStatus;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for SaleReturn domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface SaleReturnRepositoryPort {
    
    /**
     * Saves a sale return (create or update).
     *
     * @param saleReturn the sale return to save
     * @return the saved sale return with generated ID if new
     */
    SaleReturn save(SaleReturn saleReturn);
    
    /**
     * Finds a sale return by its identifier.
     *
     * @param id the sale return identifier
     * @return an Optional containing the sale return if found, empty otherwise
     */
    Optional<SaleReturn> findById(SaleReturnId id);
    
    /**
     * Retrieves all sale returns.
     *
     * @return a list of all sale returns
     */
    List<SaleReturn> findAll();
    
    /**
     * Finds all sale returns for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale returns for the sale
     */
    List<SaleReturn> findBySaleId(SaleId saleId);
    
    /**
     * Finds all sale returns with a specific status.
     *
     * @param status the sale return status
     * @return a list of sale returns with the given status
     */
    List<SaleReturn> findByStatus(SaleReturnStatus status);
    
    /**
     * Deletes a sale return by its identifier.
     *
     * @param id the sale return identifier
     */
    void deleteById(SaleReturnId id);
}
