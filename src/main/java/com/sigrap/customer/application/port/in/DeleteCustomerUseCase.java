package com.sigrap.customer.application.port.in;

import com.sigrap.customer.domain.model.CustomerId;

import java.util.List;

/**
 * Input port for deleting customers.
 * This interface defines the use cases for customer deletion operations.
 */
public interface DeleteCustomerUseCase {
    
    /**
     * Deletes a customer by their identifier.
     *
     * @param id the identifier of the customer to delete
     * @throws com.sigrap.exception.ResourceNotFoundException if the customer is not found
     */
    void delete(CustomerId id);
    
    /**
     * Deletes multiple customers by their identifiers.
     *
     * @param ids the list of customer identifiers to delete
     * @throws com.sigrap.exception.ResourceNotFoundException if any of the customers are not found
     */
    void deleteAll(List<CustomerId> ids);
}
