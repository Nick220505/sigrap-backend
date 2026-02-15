package com.sigrap.product.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.application.port.in.DeleteProductUseCase;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementing the DeleteProductUseCase.
 * This service handles product deletion operations by:
 * <ul>
 *   <li>Validating that products exist before deletion</li>
 *   <li>Delegating to the repository port for deletion</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for deletion</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class DeleteProductService implements DeleteProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param productRepository the repository port for product deletion
     */
    public DeleteProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Deletes a product by its identifier.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Product must exist before deletion</li>
     * </ul>
     * 
     * @param id the identifier of the product to delete
     * @throws ResourceNotFoundException if the product is not found
     */
    @Override
    public void delete(ProductId id) {
        // Verify product exists
        if (productRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                "Product with ID '" + id.value() + "' not found"
            );
        }
        
        // Delete through port
        productRepository.deleteById(id);
    }
    
    /**
     * Deletes multiple products by their identifiers.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>All products must exist before deletion</li>
     * </ul>
     * 
     * @param ids the list of product identifiers to delete
     * @throws ResourceNotFoundException if any of the products are not found
     */
    @Override
    public void deleteAll(List<ProductId> ids) {
        // Verify all products exist
        for (ProductId id : ids) {
            if (productRepository.findById(id).isEmpty()) {
                throw new ResourceNotFoundException(
                    "Product with ID '" + id.value() + "' not found"
                );
            }
        }
        
        // Delete all through port
        productRepository.deleteAllById(ids);
    }
}
