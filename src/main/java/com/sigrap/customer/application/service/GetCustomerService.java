package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.GetCustomerUseCase;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetCustomerUseCase.
 * This service handles customer retrieval operations by:
 * <ul>
 *   <li>Retrieving customers by ID</li>
 *   <li>Retrieving all customers</li>
 *   <li>Delegating to the repository port for data access</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for data retrieval</li>
 *   <li>Read-only transactions for performance</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class GetCustomerService implements GetCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param customerRepository the repository port for customer data access
     */
    public GetCustomerService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * Retrieves a customer by their identifier.
     * 
     * @param id the customer identifier
     * @return the customer domain entity
     * @throws ResourceNotFoundException if the customer is not found
     */
    @Override
    public Customer getById(CustomerId id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Customer with ID '" + id.value() + "' not found"
            ));
    }
    
    /**
     * Retrieves a customer by their identifier, returning an Optional.
     * 
     * @param id the customer identifier
     * @return an Optional containing the customer if found, empty otherwise
     */
    @Override
    public Optional<Customer> findById(CustomerId id) {
        return customerRepository.findById(id);
    }
    
    /**
     * Retrieves all customers.
     * 
     * @return a list of all customer domain entities, empty list if none exist
     */
    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
}
