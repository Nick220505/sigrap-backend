package com.sigrap.category.application.port.in;

import com.sigrap.category.domain.model.CategoryId;

import java.util.List;

/**
 * Input port for deleting categories.
 * This interface defines the use cases for category deletion operations.
 */
public interface DeleteCategoryUseCase {
    
    /**
     * Deletes a category by its identifier.
     *
     * @param id the identifier of the category to delete
     * @throws IllegalArgumentException if the category is not found
     */
    void delete(CategoryId id);
    
    /**
     * Deletes multiple categories by their identifiers.
     *
     * @param ids the list of category identifiers to delete
     * @throws IllegalArgumentException if any of the categories are not found
     */
    void deleteAll(List<CategoryId> ids);
}
