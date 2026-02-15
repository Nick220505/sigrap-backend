package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.CreateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateCustomerUseCase.
 * This service orchestrates the creation of a new customer by:
 * <ul>
 *   <li>Validating business rules (e.g., unique customer email)</li>
 *   <li>Creating the domain entity</li>
 *   <li>Persisting through the repository port</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class CreateCustomerService implements CreateCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param customerRepository the repository port for customer persistence
     */
    public CreateCustomerService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * Creates a new customer with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Customer email must be unique</li>
     *   <li>Customer email must be valid (enforced by CustomerEmail value object)</li>
     *   <li>Customer name must be valid (enforced by CustomerName value object)</li>
     *   <li>Customer phone must be valid if provided (enforced by CustomerPhone value object)</li>
     * </ul>
     *
     * @param command the command containing customer creation data
     * @return the created customer domain entity with generated ID
     * @throws IllegalArgumentException if a customer with the same email already exists
     * @throws IllegalArgumentException if any value object validation fails
     */
    @Override
    public Customer create(CreateCustomerCommand command) {
        // Create value objects (validates format)
        CustomerName fullName = new CustomerName(command.fullName());
        CustomerEmail email = new CustomerEmail(command.email());
        CustomerPhone phoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
            ? new CustomerPhone(command.phoneNumber())
            : null;
        
        // Business rule: customer email must be unique
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Customer with email '" + email.value() + "' already exists"
            );
        }
        
        // Create domain entity
        Customer customer = new Customer(
            fullName,
            command.documentId(),
            email,
            phoneNumber,
            command.address()
        );
        
        // Persist through port and return with generated ID
        return customerRepository.save(customer);
    }
}
