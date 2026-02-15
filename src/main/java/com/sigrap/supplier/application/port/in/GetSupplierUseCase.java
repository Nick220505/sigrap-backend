package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving suppliers.
 * This interface defines the use cases for supplier retrieval operations.
 */
public interface GetSupplierUseCase {
    
    /**
     * Retrieves a supplier by its identifier.
     * Throws an exception if the supplier is not found.
     *
     * @param id the supplier identifier
     * @return the supplier domain entity
     * @throws IllegalArgumentException if the supplier is not found
     */
    Supplier getById(SupplierId id);
    
    /**
     * Finds a supplier by its identifier.
     * Returns an empty Optional if the supplier is not found.
     *
     * @param id the supplier identifier
     * @return an Optional containing the supplier if found, empty otherwise
     */
    Optional<Supplier> findById(SupplierId id);
    
    /**
     * Retrieves all suppliers.
     *
     * @return a list of all suppliers, empty list if none exist
     */
    List<Supplier> getAll();
}
