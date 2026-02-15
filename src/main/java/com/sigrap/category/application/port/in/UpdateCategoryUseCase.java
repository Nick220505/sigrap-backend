package com.sigrap.category.application.port.in;

import com.sigrap.category.application.port.in.command.UpdateCategoryCommand;
import com.sigrap.category.domain.model.Category;
import com.sigrap.category.domain.model.CategoryId;

/**
 * Input port for updating an existing category.
 * This interface defines the use case for category update operations.
 */
public interface UpdateCategoryUseCase {
    
    /**
     * Updates an existing category with the provided command data.
     *
     * @param id the identifier of the category to update
     * @param command the command containing category update data
     * @return the updated category domain entity
     * @throws IllegalArgumentException if the category is not found or if the new name conflicts with an existing category
     */
    Category update(CategoryId id, UpdateCategoryCommand command);
}
