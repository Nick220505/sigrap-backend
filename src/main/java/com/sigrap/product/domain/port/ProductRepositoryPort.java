package com.sigrap.product.domain.port;

import com.sigrap.category.domain.model.CategoryId;
import com.sigrap.product.domain.model.Product;
import com.sigrap.product.domain.model.ProductId;
import com.sigrap.product.domain.model.ProductName;
import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Product domain entity.
 * Defines the contract for persistence operations without exposing implementation details.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface ProductRepositoryPort {
    
    /**
     * Saves a product (create or update).
     *
     * @param product the product to save
     * @return the saved product with generated ID if new
     */
    Product save(Product product);
    
    /**
     * Finds a product by its identifier.
     *
     * @param id the product identifier
     * @return an Optional containing the product if found, empty otherwise
     */
    Optional<Product> findById(ProductId id);
    
    /**
     * Retrieves all products.
     *
     * @return a list of all products
     */
    List<Product> findAll();
    
    /**
     * Finds all products belonging to a specific category.
     *
     * @param categoryId the category identifier
     * @return a list of products in the category
     */
    List<Product> findByCategoryId(CategoryId categoryId);
    
    /**
     * Checks if a product with the given name exists.
     *
     * @param name the product name to check
     * @return true if a product with the name exists, false otherwise
     */
    boolean existsByName(ProductName name);
    
    /**
     * Checks if a product with the given name exists, excluding a specific product ID.
     * Useful for update operations to check name uniqueness.
     *
     * @param name the product name to check
     * @param excludeId the product ID to exclude from the check
     * @return true if another product with the name exists, false otherwise
     */
    boolean existsByNameAndIdNot(ProductName name, ProductId excludeId);
    
    /**
     * Deletes a product by its identifier.
     *
     * @param id the product identifier
     */
    void deleteById(ProductId id);
    
    /**
     * Deletes multiple products by their identifiers.
     *
     * @param ids the list of product identifiers to delete
     */
    void deleteAllById(List<ProductId> ids);


    /**
     * Counts the total number of products.
     *
     * @return the total count of products
     */
    long count();

    /**
     * Saves multiple products at once.
     * Useful for batch operations.
     *
     * @param products the list of products to save
     * @return the list of saved products
     */
    List<Product> saveAll(List<Product> products);

}
