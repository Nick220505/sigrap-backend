package com.sigrap.product.application.service;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.application.port.in.GetProductUseCase;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetProductUseCase.
 * This service handles product retrieval operations by:
 * <ul>
 *   <li>Retrieving products by ID</li>
 *   <li>Retrieving all products</li>
 *   <li>Retrieving products by category</li>
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
public class GetProductService implements GetProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param productRepository the repository port for product data access
     */
    public GetProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Retrieves a product by its identifier.
     * 
     * @param id the product identifier
     * @return the product domain entity
     * @throws ResourceNotFoundException if the product is not found
     */
    @Override
    public Product getById(ProductId id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product with ID '" + id.value() + "' not found"
            ));
    }
    
    /**
     * Retrieves a product by its identifier, returning an Optional.
     * 
     * @param id the product identifier
     * @return an Optional containing the product if found, empty otherwise
     */
    @Override
    public Optional<Product> findById(ProductId id) {
        return productRepository.findById(id);
    }
    
    /**
     * Retrieves all products.
     * 
     * @return a list of all product domain entities, empty list if none exist
     */
    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
    
    /**
     * Retrieves all products belonging to a specific category.
     * 
     * @param categoryId the category identifier
     * @return a list of products in the category, empty list if none exist
     */
    @Override
    public List<Product> getByCategoryId(CategoryId categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
