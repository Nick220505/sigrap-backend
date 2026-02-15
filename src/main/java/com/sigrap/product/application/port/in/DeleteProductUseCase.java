package com.sigrap.product.application.port.in;

import com.sigrap.product.domain.model.ProductId;

import java.util.List;

/**
 * Input port for deleting products.
 * This interface defines the use cases for product deletion operations.
 */
public interface DeleteProductUseCase {
    
    /**
     * Deletes a product by its identifier.
     *
     * @param id the identifier of the product to delete
     * @throws IllegalArgumentException if the product is not found
     */
    void delete(ProductId id);
    
    /**
     * Deletes multiple products by their identifiers.
     *
     * @param ids the list of product identifiers to delete
     * @throws IllegalArgumentException if any of the products are not found
     */
    void deleteAll(List<ProductId> ids);
}
