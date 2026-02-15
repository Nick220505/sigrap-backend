package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.DeleteSupplierUseCase;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementing the DeleteSupplierUseCase.
 * This service handles supplier deletion operations.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 * </ul>
 */
@Service
@Transactional
public class DeleteSupplierService implements DeleteSupplierUseCase {
    
    private final SupplierRepositoryPort supplierRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param supplierRepository the repository port for supplier persistence
     */
    public DeleteSupplierService(SupplierRepositoryPort supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Deletes a supplier by its identifier.
     * Verifies the supplier exists before deletion.
     *
     * @param id the supplier identifier
     * @throws IllegalArgumentException if the supplier is not found
     */
    @Override
    public void delete(SupplierId id) {
        // Verify supplier exists
        if (supplierRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException(
                "Supplier with ID " + id.value() + " not found"
            );
        }
        
        supplierRepository.deleteById(id);
    }
    
    /**
     * Deletes multiple suppliers by their identifiers.
     * Useful for batch operations.
     *
     * @param ids the list of supplier identifiers to delete
     */
    @Override
    public void deleteAll(List<SupplierId> ids) {
        supplierRepository.deleteAllById(ids);
    }
}
