package com.sigrap.product.application.port.in;

import com.sigrap.product.application.port.in.command.CreateProductCommand;
import com.sigrap.product.domain.model.Product;

/**
 * Input port for creating a new product.
 * This interface defines the use case for product creation.
 */
public interface CreateProductUseCase {
    
    /**
     * Creates a new product with the provided command data.
     *
     * @param command the command containing product creation data
     * @return the created product domain entity
     * @throws IllegalArgumentException if a product with the same name already exists
     * @throws IllegalArgumentException if the category does not exist (when categoryId is provided)
     */
    Product create(CreateProductCommand command);
}
