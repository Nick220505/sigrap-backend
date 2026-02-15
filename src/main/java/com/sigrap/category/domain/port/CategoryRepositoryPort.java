package com.sigrap.category.domain.port;

import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.category.domain.model.CategoryName;

import java.util.List;
import java.util.Optional;

/**
 * Repository port (output port) for Category aggregate.
 * This interface is defined in the domain layer and expresses domain needs,
 * not database operations. It will be implemented by infrastructure adapters.
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Defined in domain layer (no framework dependencies)</li>
 *   <li>Returns domain entities, not persistence entities</li>
 *   <li>Uses domain value objects for parameters</li>
 *   <li>Expresses business intent, not technical operations</li>
 * </ul>
 */
public interface CategoryRepositoryPort {

    /**
     * Saves a category (create or update).
     * If the category is new (no ID), it will be created.
     * If the category has an ID, it will be updated.
     *
     * @param category the category to save
     * @return the saved category with generated ID if it was new
     */
    Category save(Category category);

    /**
     * Finds a category by its identifier.
     *
     * @param id the category identifier
     * @return an Optional containing the category if found, empty otherwise
     */
    Optional<Category> findById(CategoryId id);

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories, empty list if none exist
     */
    List<Category> findAll();

    /**
     * Checks if a category with the given name exists.
     * Used to enforce business rule: category names must be unique.
     *
     * @param name the category name to check
     * @return true if a category with this name exists, false otherwise
     */
    boolean existsByName(CategoryName name);

    /**
     * Deletes a category by its identifier.
     *
     * @param id the category identifier
     */
    void deleteById(CategoryId id);

    /**
     * Deletes multiple categories by their identifiers.
     * Useful for batch operations.
     *
     * @param ids the list of category identifiers to delete
     */
    void deleteAllById(List<CategoryId> ids);
}
