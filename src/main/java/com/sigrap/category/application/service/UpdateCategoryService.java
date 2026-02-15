package com.sigrap.category.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityUpdatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.category.application.port.in.UpdateCategoryUseCase;
import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the UpdateCategoryUseCase.
 * This service orchestrates the update of an existing category by:
 * <ul>
 *   <li>Retrieving the existing category</li>
 *   <li>Validating business rules (e.g., unique category name if changed)</li>
 *   <li>Updating the domain entity</li>
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
public class UpdateCategoryService implements UpdateCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public UpdateCategoryService(CategoryRepositoryPort categoryRepository, EventPublisherPort eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Updates an existing category with the provided command data.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Category must exist</li>
     *   <li>Category name must be unique (if changed)</li>
     *   <li>Category name must be valid (enforced by CategoryName value object)</li>
     * </ul>
     *
     * @param id the identifier of the category to update
     * @param command the command containing category update data
     * @return the updated category domain entity
     * @throws ResourceNotFoundException if the category is not found
     * @throws IllegalArgumentException if the new name conflicts with an existing category
     * @throws IllegalArgumentException if the category name is invalid (from CategoryName validation)
     */
    @Override
    public Category update(CategoryId id, UpdateCategoryCommand command) {
        long startTime = System.currentTimeMillis();
        
        // Retrieve existing category
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category with ID '" + id.value() + "' not found"
            ));
        
        // Create value object for new name (validates name format)
        CategoryName newName = new CategoryName(command.name());
        
        StringBuilder changes = new StringBuilder();
        
        // Business rule: if name is changing, ensure new name is unique
        if (!category.getName().equals(newName)) {
            if (categoryRepository.existsByName(newName)) {
                throw new IllegalArgumentException(
                    "Category with name '" + newName.value() + "' already exists"
                );
            }
            changes.append("Name changed from '").append(category.getName().value())
                   .append("' to '").append(newName.value()).append("'; ");
            // Update name through domain method
            category.updateName(newName);
        }
        
        // Update description through domain method
        if (!java.util.Objects.equals(category.getDescription(), command.description())) {
            changes.append("Description updated; ");
        }
        category.updateDescription(command.description());
        
        // Persist through port and return updated entity
        Category updatedCategory = categoryRepository.save(category);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityUpdatedEvent(
            EntityType.CATEGORY,
            updatedCategory.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null, // sourceIp - can be added via request context if needed
            null, // userAgent - can be added via request context if needed
            changes.length() > 0 ? changes.toString() : "Category updated",
            durationMs
        ));
        
        return updatedCategory;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
