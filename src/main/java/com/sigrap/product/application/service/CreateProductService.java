package com.sigrap.product.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.product.application.port.in.CreateProductUseCase;
import com.sigrap.product.application.port.in.command.CreateProductCommand;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductName;
import com.sigrap.product.domain.model.ProductPrice;
import com.sigrap.product.domain.model.ProductStock;
import com.sigrap.product.domain.port.ProductRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the CreateProductUseCase.
 * This service orchestrates the creation of a new product by:
 * <ul>
 *   <li>Validating business rules (e.g., unique product name)</li>
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
public class CreateProductService implements CreateProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param productRepository the repository port for product persistence
     * @param categoryRepository the repository port for category validation
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public CreateProductService(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository,
            EventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a new product with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Product name must be unique</li>
     *   <li>Product name must be valid (enforced by ProductName value object)</li>
     *   <li>Prices must be valid (enforced by ProductPrice value object)</li>
     *   <li>Stock values must be valid (enforced by ProductStock value object)</li>
     *   <li>Category must exist if provided</li>
     * </ul>
     *
     * @param command the command containing product creation data
     * @return the created product domain entity with generated ID
     * @throws IllegalArgumentException if a product with the same name already exists
     * @throws IllegalArgumentException if any value object validation fails
     * @throws ResourceNotFoundException if the category does not exist
     */
    @Override
    public Product create(CreateProductCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Create value objects (validates format)
        ProductName name = new ProductName(command.name());
        ProductPrice costPrice = new ProductPrice(command.costPrice());
        ProductPrice salePrice = new ProductPrice(command.salePrice());
        ProductStock stock = new ProductStock(command.stock());
        ProductStock minimumStockThreshold = new ProductStock(command.minimumStockThreshold());
        CategoryId categoryId = command.categoryId() != null ? new CategoryId(command.categoryId()) : null;
        
        // Business rule: product name must be unique
        if (productRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "Product with name '" + name.value() + "' already exists"
            );
        }
        
        // Business rule: category must exist if provided
        if (categoryId != null && categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResourceNotFoundException(
                "Category with ID '" + categoryId.value() + "' not found"
            );
        }
        
        // Create domain entity
        Product product = new Product(
            name,
            command.description(),
            costPrice,
            salePrice,
            stock,
            minimumStockThreshold,
            categoryId
        );
        
        // Persist through port and return with generated ID
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.PRODUCT,
            savedProduct.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Product created: " + savedProduct.getName().value(),
            durationMs
        ));
        
        return savedProduct;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
