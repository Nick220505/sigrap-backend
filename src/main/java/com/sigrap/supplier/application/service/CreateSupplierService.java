package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.CreateSupplierUseCase;
import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierPhone;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the CreateSupplierUseCase.
 * This service orchestrates the creation of a new supplier by:
 * <ul>
 *   <li>Validating business rules (e.g., unique supplier email)</li>
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
public class CreateSupplierService implements CreateSupplierUseCase {
    
    private final SupplierRepositoryPort supplierRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param supplierRepository the repository port for supplier persistence
     */
    public CreateSupplierService(SupplierRepositoryPort supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Creates a new supplier with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Supplier email must be unique</li>
     *   <li>Supplier name must be valid (enforced by SupplierName value object)</li>
     *   <li>Supplier email must be valid (enforced by SupplierEmail value object)</li>
     * </ul>
     *
     * @param command the command containing supplier creation data
     * @return the created supplier domain entity with generated ID
     * @throws IllegalArgumentException if a supplier with the same email already exists
     * @throws IllegalArgumentException if the supplier name or email is invalid (from value object validation)
     */
    @Override
    public Supplier create(CreateSupplierCommand command) {
        // Create value objects (validates format)
        SupplierName name = new SupplierName(command.name());
        SupplierEmail email = new SupplierEmail(command.email());
        SupplierPhone phone = command.phone() != null && !command.phone().isBlank() 
            ? new SupplierPhone(command.phone()) 
            : null;
        
        // Business rule: supplier email must be unique
        if (supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Supplier with email '" + email.value() + "' already exists"
            );
        }
        
        // Create domain entity
        Supplier supplier = new Supplier(
            name,
            command.contactName(),
            email,
            phone,
            command.address()
        );
        
        // Persist through port and return with generated ID
        return supplierRepository.save(supplier);
    }
}
