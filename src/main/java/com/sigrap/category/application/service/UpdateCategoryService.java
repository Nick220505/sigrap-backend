package com.sigrap.category.application.service;

import com.sigrap.category.application.port.in.UpdateCategoryUseCase;
import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category persistence
     */
    public UpdateCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
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
        // Retrieve existing category
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category with ID '" + id.value() + "' not found"
            ));
        
        // Create value object for new name (validates name format)
        CategoryName newName = new CategoryName(command.name());
        
        // Business rule: if name is changing, ensure new name is unique
        if (!category.getName().equals(newName)) {
            if (categoryRepository.existsByName(newName)) {
                throw new IllegalArgumentException(
                    "Category with name '" + newName.value() + "' already exists"
                );
            }
            // Update name through domain method
            category.updateName(newName);
        }
        
        // Update description through domain method
        category.updateDescription(command.description());
        
        // Persist through port and return updated entity
        return categoryRepository.save(category);
    }
}
