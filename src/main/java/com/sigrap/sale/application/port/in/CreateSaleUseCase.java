package com.sigrap.sale.application.port.in;

import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.domain.model.Sale;

/**
 * Input port for creating a new sale.
 * This interface defines the use case for sale creation.
 */
public interface CreateSaleUseCase {
    
    /**
     * Creates a new sale with the provided command data.
     *
     * @param command the command containing sale creation data
     * @return the created sale domain entity
     */
    Sale create(CreateSaleCommand command);
}
