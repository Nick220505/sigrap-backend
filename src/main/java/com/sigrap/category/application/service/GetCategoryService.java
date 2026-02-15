package com.sigrap.category.application.service;

import com.sigrap.category.application.port.in.GetCategoryUseCase;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.port.CategoryRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementing the GetCategoryUseCase.
 * This service handles category retrieval operations by:
 * <ul>
 *   <li>Retrieving categories by ID</li>
 *   <li>Retrieving all categories</li>
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
public class GetCategoryService implements GetCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    
    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the repository port for category data access
     */
    public GetCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Retrieves a category by its identifier.
     * 
     * @param id the category identifier
     * @return the category domain entity
     * @throws ResourceNotFoundException if the category is not found
     */
    @Override
    public Category getById(CategoryId id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category with ID '" + id.value() + "' not found"
            ));
    }
    
    /**
     * Retrieves a category by its identifier, returning an Optional.
     * 
     * @param id the category identifier
     * @return an Optional containing the category if found, empty otherwise
     */
    @Override
    public Optional<Category> findById(CategoryId id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Retrieves all categories.
     * 
     * @return a list of all category domain entities, empty list if none exist
     */
    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
