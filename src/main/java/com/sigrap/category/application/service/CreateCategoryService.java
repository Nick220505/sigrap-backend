package com.sigrap.category.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.category.application.port.in.CreateCategoryUseCase;
import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the CreateCategoryUseCase.
 * This service orchestrates the creation of a new category by:
 * <ul>
 *   <li>Validating business rules (e.g., unique category name)</li>
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
public class CreateCategoryService implements CreateCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public CreateCategoryService(CategoryRepositoryPort categoryRepository, EventPublisherPort eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a new category with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Category name must be unique</li>
     *   <li>Category name must be valid (enforced by CategoryName value object)</li>
     * </ul>
     *
     * @param command the command containing category creation data
     * @return the created category domain entity with generated ID
     * @throws IllegalArgumentException if a category with the same name already exists
     * @throws IllegalArgumentException if the category name is invalid (from CategoryName validation)
     */
    @Override
    public Category create(CreateCategoryCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Create value object (validates name format)
        CategoryName name = new CategoryName(command.name());
        
        // Business rule: category name must be unique
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                "Category with name '" + name.value() + "' already exists"
            );
        }
        
        // Create domain entity
        Category category = new Category(name, command.description());
        
        // Persist through port and return with generated ID
        Category savedCategory = categoryRepository.save(category);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.CATEGORY,
            savedCategory.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null, // sourceIp - can be added via request context if needed
            null, // userAgent - can be added via request context if needed
            "Category created: " + savedCategory.getName().value(),
            durationMs
        ));
        
        return savedCategory;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
