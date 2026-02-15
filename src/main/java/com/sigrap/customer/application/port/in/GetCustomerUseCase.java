package com.sigrap.customer.application.port.in;

import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving customers.
 * This interface defines the use cases for customer retrieval operations.
 */
public interface GetCustomerUseCase {
    
    /**
     * Retrieves a customer by their identifier.
     *
     * @param id the customer identifier
     * @return the customer domain entity
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    Customer getById(CustomerId id);
    
    /**
     * Retrieves a customer by their identifier, returning an Optional.
     *
     * @param id the customer identifier
     * @return an Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findById(CustomerId id);
    
    /**
     * Retrieves all customers.
     *
     * @return a list of all customer domain entities
     */
    List<Customer> getAll();
}
