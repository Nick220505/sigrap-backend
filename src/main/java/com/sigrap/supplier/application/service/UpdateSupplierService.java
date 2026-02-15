package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.UpdateSupplierUseCase;
import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierPhone;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateSupplierUseCase.
 * This service orchestrates the update of an existing supplier.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Enforces business rules</li>
 * </ul>
 */
@Service
@Transactional
public class UpdateSupplierService implements UpdateSupplierUseCase {
    
    private final SupplierRepositoryPort supplierRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param supplierRepository the repository port for supplier persistence
     */
    public UpdateSupplierService(SupplierRepositoryPort supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Updates an existing supplier with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Supplier must exist</li>
     *   <li>Supplier email must be unique (excluding the current supplier)</li>
     *   <li>Supplier name must be valid (enforced by SupplierName value object)</li>
     *   <li>Supplier email must be valid (enforced by SupplierEmail value object)</li>
     * </ul>
     *
     * @param id the identifier of the supplier to update
     * @param command the command containing updated supplier data
     * @return the updated supplier domain entity
     * @throws IllegalArgumentException if the supplier is not found
     * @throws IllegalArgumentException if the new email is already used by another supplier
     */
    @Override
    public Supplier update(SupplierId id, UpdateSupplierCommand command) {
        // Retrieve existing supplier
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Supplier with ID " + id.value() + " not found"
            ));
        
        // Create value objects (validates format)
        SupplierName name = new SupplierName(command.name());
        SupplierEmail email = new SupplierEmail(command.email());
        SupplierPhone phone = command.phone() != null && !command.phone().isBlank() 
            ? new SupplierPhone(command.phone()) 
            : null;
        
        // Business rule: supplier email must be unique (excluding current supplier)
        if (!supplier.getEmail().equals(email) && 
            supplierRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException(
                "Supplier with email '" + email.value() + "' already exists"
            );
        }
        
        // Update domain entity using business methods
        supplier.updateName(name);
        supplier.updateContactName(command.contactName());
        supplier.updateEmail(email);
        supplier.updatePhone(phone);
        supplier.updateAddress(command.address());
        
        // Persist through port
        return supplierRepository.save(supplier);
    }
}
