package com.sigrap.supplier.application.port.in;

import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;

/**
 * Input port for creating a new supplier.
 * This interface defines the use case for supplier creation.
 */
public interface CreateSupplierUseCase {
    
    /**
     * Creates a new supplier with the provided command data.
     *
     * @param command the command containing supplier creation data
     * @return the created supplier domain entity
     * @throws IllegalArgumentException if a supplier with the same email already exists
     */
    Supplier create(CreateSupplierCommand command);
}
