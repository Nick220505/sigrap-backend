package com.sigrap.customer.application.port.in;

import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;

/**
 * Input port for updating an existing customer.
 * This interface defines the use case for customer update operations.
 */
public interface UpdateCustomerUseCase {
    
    /**
     * Updates an existing customer with the provided command data.
     *
     * @param id the identifier of the customer to update
     * @param command the command containing customer update data
     * @return the updated customer domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     * @throws IllegalArgumentException if the new email conflicts with an existing customer
     */
    Customer update(CustomerId id, UpdateCustomerCommand command);
}
