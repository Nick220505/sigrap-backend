package com.sigrap.category.application.port.in;

import com.sigrap.category.application.port.in.command.CreateCategoryCommand;
import com.sigrap.category.domain.model.Category;

/**
 * Input port for creating a new category.
 * This interface defines the use case for category creation.
 */
public interface CreateCategoryUseCase {
    
    /**
     * Creates a new category with the provided command data.
     *
     * @param command the command containing category creation data
     * @return the created category domain entity
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    Category create(CreateCategoryCommand command);
}
