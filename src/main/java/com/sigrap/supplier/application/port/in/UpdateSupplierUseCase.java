package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;

/**
 * Input port for updating an existing supplier.
 * This interface defines the use case for supplier updates.
 */
public interface UpdateSupplierUseCase {
    
    /**
     * Updates an existing supplier with the provided command data.
     *
     * @param id the identifier of the supplier to update
     * @param command the command containing updated supplier data
     * @return the updated supplier domain entity
     * @throws IllegalArgumentException if the supplier is not found
     * @throws IllegalArgumentException if the new email is already used by another supplier
     */
    Supplier update(SupplierId id, UpdateSupplierCommand command);
}
