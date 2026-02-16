package com.sigrap.category.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.BulkEntityDeletedEvent;
import com.sigrap.audit.domain.event.EntityDeletedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.category.application.port.in.DeleteCategoryUseCase;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementing the DeleteCategoryUseCase.
 * This service orchestrates the deletion of categories by:
 * <ul>
 *   <li>Validating that the category exists</li>
 *   <li>Deleting through the repository port</li>
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
public class DeleteCategoryService implements DeleteCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    private final EventPublisherPort eventPublisher;
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category persistence
     * @param eventPublisher the event publisher port for publishing domain events
     */
    public DeleteCategoryService(CategoryRepositoryPort categoryRepository, EventPublisherPort eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Deletes a category by its identifier.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>Category must exist before deletion</li>
     * </ul>
     *
     * @param id the identifier of the category to delete
     * @throws ResourceNotFoundException if the category is not found
     */
    @Override
    public void delete(CategoryId id) {
        long startTime = System.currentTimeMillis();
        
        // Verify category exists
        if (!categoryRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(
                "Category with ID '" + id.value() + "' not found"
            );
        }
        
        // Delete through port
        categoryRepository.deleteById(id);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityDeletedEvent(
            EntityType.CATEGORY,
            id.value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null, // sourceIp - can be added via request context if needed
            null, // userAgent - can be added via request context if needed
            "Category deleted",
            durationMs
        ));
    }
    
    /**
     * Deletes multiple categories by their identifiers.
     * 
     * <p>Business rules enforced:
     * <ul>
     *   <li>All categories must exist before deletion</li>
     * </ul>
     *
     * @param ids the list of category identifiers to delete
     * @throws ResourceNotFoundException if any of the categories are not found
     */
    @Override
    public void deleteAll(List<CategoryId> ids) {
        // Handle empty list gracefully
        if (ids == null || ids.isEmpty()) {
            categoryRepository.deleteAllById(List.of());
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        // Verify all categories exist
        for (CategoryId id : ids) {
            if (!categoryRepository.findById(id).isPresent()) {
                throw new ResourceNotFoundException(
                    "Category with ID '" + id.value() + "' not found"
                );
            }
        }
        
        // Delete all through port
        categoryRepository.deleteAllById(ids);
        
        // Publish domain event for audit logging
        long durationMs = System.currentTimeMillis() - startTime;
        List<String> entityIds = ids.stream()
            .map(id -> id.value().toString())
            .toList();
        eventPublisher.publish(new BulkEntityDeletedEvent(
            EntityType.CATEGORY,
            entityIds,
            getCurrentUsername(),
            LocalDateTime.now(),
            null, // sourceIp - can be added via request context if needed
            null, // userAgent - can be added via request context if needed
            durationMs
        ));
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
