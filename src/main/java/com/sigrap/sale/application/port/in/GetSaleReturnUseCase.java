package com.sigrap.sale.application.port.in;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnStatus;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving sale returns.
 * This interface defines the use cases for sale return retrieval operations.
 */
public interface GetSaleReturnUseCase {
    
    /**
     * Retrieves a sale return by its identifier.
     *
     * @param id the sale return identifier
     * @return the sale return domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale return is not found
     */
    SaleReturn getById(SaleReturnId id);
    
    /**
     * Retrieves a sale return by its identifier, returning an Optional.
     *
     * @param id the sale return identifier
     * @return an Optional containing the sale return if found, empty otherwise
     */
    Optional<SaleReturn> findById(SaleReturnId id);
    
    /**
     * Retrieves all sale returns.
     *
     * @return a list of all sale return domain entities
     */
    List<SaleReturn> getAll();
    
    /**
     * Retrieves all sale returns for a specific sale.
     *
     * @param saleId the sale identifier
     * @return a list of sale returns for the sale
     */
    List<SaleReturn> getBySaleId(SaleId saleId);
    
    /**
     * Retrieves all sale returns with a specific status.
     *
     * @param status the sale return status
     * @return a list of sale returns with the given status
     */
    List<SaleReturn> getByStatus(SaleReturnStatus status);
}
