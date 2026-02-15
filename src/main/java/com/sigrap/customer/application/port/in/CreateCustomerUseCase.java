package com.sigrap.customer.application.port.in;

import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;

/**
 * Input port for creating a new customer.
 * This interface defines the use case for customer creation.
 */
public interface CreateCustomerUseCase {
    
    /**
     * Creates a new customer with the provided command data.
     *
     * @param command the command containing customer creation data
     * @return the created customer domain entity
     * @throws IllegalArgumentException if a customer with the same email already exists
     */
    Customer create(CreateCustomerCommand command);
}
