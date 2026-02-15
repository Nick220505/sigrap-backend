package com.sigrap.sale.application.port.in;

import com.sigrap.sale.application.port.in.command.CreateSaleReturnCommand;
import com.sigrap.sale.domain.model.SaleReturn;

/**
 * Input port for creating a new sale return.
 * This interface defines the use case for sale return creation.
 */
public interface CreateSaleReturnUseCase {
    
    /**
     * Creates a new sale return with the provided command data.
     *
     * @param command the command containing sale return creation data
     * @return the created sale return domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the sale is not found
     */
    SaleReturn create(CreateSaleReturnCommand command);
}
