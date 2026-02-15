package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.DeleteCustomerUseCase;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementing the DeleteCustomerUseCase.
 * This service orchestrates the deletion of customers by:
 * <ul>
 *   <li>Validating that the customer exists</li>
 *   <li>Deleting through the repository port</li>
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
public class DeleteCustomerService implements DeleteCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param customerRepository the repository port for customer persistence
     */
    public DeleteCustomerService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    /**
     * Deletes a customer by their identifier.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Customer must exist before deletion</li>
     * </ul>
     *
     * @param id the identifier of the customer to delete
     * @throws ResourceNotFoundException if the customer is not found
     */
    @Override
    public void delete(CustomerId id) {
        // Verify customer exists
        if (!customerRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(
                "Customer with ID '" + id.value() + "' not found"
            );
        }
        
        // Delete through port
        customerRepository.deleteById(id);
    }
    
    /**
     * Deletes multiple customers by their identifiers.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>All customers must exist before deletion</li>
     * </ul>
     *
     * @param ids the list of customer identifiers to delete
     * @throws ResourceNotFoundException if any of the customers are not found
     */
    @Override
    public void deleteAll(List<CustomerId> ids) {
        // Verify all customers exist
        for (CustomerId id : ids) {
            if (!customerRepository.findById(id).isPresent()) {
                throw new ResourceNotFoundException(
                    "Customer with ID '" + id.value() + "' not found"
                );
            }
        }
        
        // Delete all through port
        customerRepository.deleteAllById(ids);
    }
}
