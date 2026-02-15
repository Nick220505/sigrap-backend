package com.sigrap.category.application.port.in;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving categories.
 * This interface defines the use cases for category retrieval operations.
 */
public interface GetCategoryUseCase {
    
    /**
     * Retrieves a category by its identifier.
     *
     * @param id the category identifier
     * @return the category domain entity
     * @throws IllegalArgumentException if the category is not found
     */
    Category getById(CategoryId id);
    
    /**
     * Retrieves a category by its identifier, returning an Optional.
     *
     * @param id the category identifier
     * @return an Optional containing the category if found, empty otherwise
     */
    Optional<Category> findById(CategoryId id);
    
    /**
     * Retrieves all categories.
     *
     * @return a list of all category domain entities
     */
    List<Category> getAll();
}
