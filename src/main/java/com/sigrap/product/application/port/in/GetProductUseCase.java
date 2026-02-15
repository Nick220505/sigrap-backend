package com.sigrap.product.application.port.in;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving products.
 * This interface defines the use cases for product retrieval operations.
 */
public interface GetProductUseCase {
    
    /**
     * Retrieves a product by its identifier.
     *
     * @param id the product identifier
     * @return the product domain entity
     * @throws IllegalArgumentException if the product is not found
     */
    Product getById(ProductId id);
    
    /**
     * Retrieves a product by its identifier, returning an Optional.
     *
     * @param id the product identifier
     * @return an Optional containing the product if found, empty otherwise
     */
    Optional<Product> findById(ProductId id);
    
    /**
     * Retrieves all products.
     *
     * @return a list of all product domain entities
     */
    List<Product> getAll();
    
    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId the category identifier
     * @return a list of products in the category
     */
    List<Product> getByCategoryId(CategoryId categoryId);
}
