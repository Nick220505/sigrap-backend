package com.sigrap.customer.domain.port;

import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerEmail;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Customer domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface CustomerRepositoryPort {
    
    /**
     * Saves a customer (create or update).
     *
     * @param customer the customer to save
     * @return the saved customer with generated ID if new
     */
    Customer save(Customer customer);
    
    /**
     * Finds a customer by their identifier.
     *
     * @param id the customer identifier
     * @return an Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findById(CustomerId id);
    
    /**
     * Retrieves all customers.
     *
     * @return a list of all customers
     */
    List<Customer> findAll();
    
    /**
     * Checks if a customer with the given email exists.
     *
     * @param email the customer email to check
     * @return true if a customer with the email exists, false otherwise
     */
    boolean existsByEmail(CustomerEmail email);
    
    /**
     * Checks if a customer with the given email exists, excluding a specific customer ID.
     * Useful for update operations to check email uniqueness.
     *
     * @param email the customer email to check
     * @param excludeId the customer ID to exclude from the check
     * @return true if another customer with the email exists, false otherwise
     */
    boolean existsByEmailAndIdNot(CustomerEmail email, CustomerId excludeId);
    
    /**
     * Deletes a customer by their identifier.
     *
     * @param id the customer identifier
     */
    void deleteById(CustomerId id);
    
    /**
     * Deletes multiple customers by their identifiers.
     *
     * @param ids the list of customer identifiers to delete
     */
    void deleteAllById(List<CustomerId> ids);
}
