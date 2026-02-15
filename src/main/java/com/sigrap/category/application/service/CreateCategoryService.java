package com.sigrap.category.application.service;

import com.sigrap.category.application.port.in.CreateCategoryUseCase;
import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryName;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category persistence
     */
    public CreateCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
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
        return categoryRepository.save(category);
    }
}
