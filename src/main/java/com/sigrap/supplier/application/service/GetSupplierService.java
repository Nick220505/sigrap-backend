package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.GetSupplierUseCase;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetSupplierUseCase.
 * This service handles supplier retrieval operations.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for data access</li>
 *   <li>Read-only operations use @Transactional(readOnly = true)</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class GetSupplierService implements GetSupplierUseCase {
    
    private final SupplierRepositoryPort supplierRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param supplierRepository the repository port for supplier data access
     */
    public GetSupplierService(SupplierRepositoryPort supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Retrieves a supplier by its identifier.
     * Throws an exception if the supplier is not found.
     *
     * @param id the supplier identifier
     * @return the supplier domain entity
     * @throws IllegalArgumentException if the supplier is not found
     */
    @Override
    public Supplier getById(SupplierId id) {
        return supplierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Supplier with ID " + id.value() + " not found"
            ));
    }
    
    /**
     * Finds a supplier by its identifier.
     * Returns an empty Optional if the supplier is not found.
     *
     * @param id the supplier identifier
     * @return an Optional containing the supplier if found, empty otherwise
     */
    @Override
    public Optional<Supplier> findById(SupplierId id) {
        return supplierRepository.findById(id);
    }
    
    /**
     * Retrieves all suppliers.
     *
     * @return a list of all suppliers, empty list if none exist
     */
    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }
}
