package com.sigrap.product.application.port.in;

import com.sigrap.product.application.port.in.command.UpdateProductCommand;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;

/**
 * Input port for updating an existing product.
 * This interface defines the use case for product update operations.
 */
public interface UpdateProductUseCase {
    
    /**
     * Updates an existing product with the provided command data.
     *
     * @param id the identifier of the product to update
     * @param command the command containing product update data
     * @return the updated product domain entity
     * @throws IllegalArgumentException if the product is not found
     * @throws IllegalArgumentException if the new name conflicts with an existing product
     * @throws IllegalArgumentException if the category does not exist (when categoryId is provided)
     */
    Product update(ProductId id, UpdateProductCommand command);
}
