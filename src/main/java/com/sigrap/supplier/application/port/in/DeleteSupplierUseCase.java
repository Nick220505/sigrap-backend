package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.domain.model.SupplierId;

import java.util.List;

/**
 * Input port for deleting suppliers.
 * This interface defines the use cases for supplier deletion operations.
 */
public interface DeleteSupplierUseCase {
    
    /**
     * Deletes a supplier by its identifier.
     *
     * @param id the supplier identifier
     * @throws IllegalArgumentException if the supplier is not found
     */
    void delete(SupplierId id);
    
    /**
     * Deletes multiple suppliers by their identifiers.
     * Useful for batch operations.
     *
     * @param ids the list of supplier identifiers to delete
     */
    void deleteAll(List<SupplierId> ids);
}
