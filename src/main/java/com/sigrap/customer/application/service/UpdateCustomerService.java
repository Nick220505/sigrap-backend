package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.UpdateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateCustomerUseCase.
 * This service orchestrates the update of an existing customer by:
 * <ul>
 *   <li>Retrieving the existing customer</li>
 *   <li>Validating business rules (e.g., unique customer email if changed)</li>
 *   <li>Updating the domain entity</li>
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
public class UpdateCustomerService implements UpdateCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param customerRepository the repository port for customer persistence
     */
    public UpdateCustomerService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * Updates an existing customer with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Customer must exist</li>
     *   <li>Customer email must be unique (if changed)</li>
     *   <li>Customer email must be valid (enforced by CustomerEmail value object)</li>
     *   <li>Customer name must be valid (enforced by CustomerName value object)</li>
     *   <li>Customer phone must be valid if provided (enforced by CustomerPhone value object)</li>
     * </ul>
     *
     * @param id the identifier of the customer to update
     * @param command the command containing customer update data
     * @return the updated customer domain entity
     * @throws ResourceNotFoundException if the customer is not found
     * @throws IllegalArgumentException if the new email conflicts with an existing customer
     * @throws IllegalArgumentException if any value object validation fails
     */
    @Override
    public Customer update(CustomerId id, UpdateCustomerCommand command) {
        // Retrieve existing customer
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Customer with ID '" + id.value() + "' not found"
            ));
        
        // Create value objects for new data (validates format)
        CustomerName newFullName = new CustomerName(command.fullName());
        CustomerEmail newEmail = new CustomerEmail(command.email());
        CustomerPhone newPhoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
            ? new CustomerPhone(command.phoneNumber())
            : null;
        
        // Business rule: if email is changing, ensure new email is unique
        if (!customer.getEmail().equals(newEmail)) {
            if (customerRepository.existsByEmailAndIdNot(newEmail, id)) {
                throw new IllegalArgumentException(
                    "Customer with email '" + newEmail.value() + "' already exists"
                );
            }
            // Update email through domain method
            customer.updateEmail(newEmail);
        }
        
        // Update other fields through domain methods
        if (!customer.getFullName().equals(newFullName)) {
            customer.updateFullName(newFullName);
        }
        
        customer.updateDocumentId(command.documentId());
        customer.updatePhoneNumber(newPhoneNumber);
        customer.updateAddress(command.address());
        
        // Persist through port and return updated entity
        return customerRepository.save(customer);
    }
}
