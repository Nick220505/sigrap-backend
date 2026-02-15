package com.sigrap.product.application.service;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.application.port.in.UpdateProductUseCase;
import com.sigrap.product.application.port.in.command.UpdateProductCommand;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.model.ProductName;
import com.sigrap.product.domain.model.ProductPrice;
import com.sigrap.product.domain.model.ProductStock;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the UpdateProductUseCase.
 * This service orchestrates the update of an existing product by:
 * <ul>
 *   <li>Retrieving the existing product</li>
 *   <li>Validating business rules (e.g., unique product name)</li>
 *   <li>Updating the domain entity through business methods</li>
 *   <li>Persisting changes through the repository port</li>
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
public class UpdateProductService implements UpdateProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param productRepository the repository port for product persistence
     * @param categoryRepository the repository port for category validation
     */
    public UpdateProductService(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Updates an existing product with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Product must exist</li>
     *   <li>Product name must be unique (excluding current product)</li>
     *   <li>All value objects must be valid</li>
     *   <li>Category must exist if provided</li>
     * </ul>
     *
     * @param id the identifier of the product to update
     * @param command the command containing product update data
     * @return the updated product domain entity
     * @throws ResourceNotFoundException if the product is not found
     * @throws ResourceNotFoundException if the category does not exist
     * @throws IllegalArgumentException if the new name conflicts with an existing product
     * @throws IllegalArgumentException if any value object validation fails
     */
    @Override
    public Product update(ProductId id, UpdateProductCommand command) {
        // Retrieve existing product
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product with ID '" + id.value() + "' not found"
            ));
        
        // Create value objects (validates format)
        ProductName newName = new ProductName(command.name());
        ProductPrice newCostPrice = new ProductPrice(command.costPrice());
        ProductPrice newSalePrice = new ProductPrice(command.salePrice());
        ProductStock newStock = new ProductStock(command.stock());
        ProductStock newMinimumStockThreshold = new ProductStock(command.minimumStockThreshold());
        CategoryId newCategoryId = command.categoryId() != null ? new CategoryId(command.categoryId()) : null;
        
        // Business rule: product name must be unique (excluding current product)
        if (!product.getName().equals(newName) && 
            productRepository.existsByNameAndIdNot(newName, id)) {
            throw new IllegalArgumentException(
                "Product with name '" + newName.value() + "' already exists"
            );
        }
        
        // Business rule: category must exist if provided
        if (newCategoryId != null && categoryRepository.findById(newCategoryId).isEmpty()) {
            throw new ResourceNotFoundException(
                "Category with ID '" + newCategoryId.value() + "' not found"
            );
        }
        
        // Update domain entity through business methods
        product.updateName(newName);
        product.updateDescription(command.description());
        product.updatePrices(newCostPrice, newSalePrice);
        product.updateStock(newStock);
        product.updateMinimumStockThreshold(newMinimumStockThreshold);
        product.updateCategory(newCategoryId);
        
        // Persist changes through port
        return productRepository.save(product);
    }
}
