package com.sigrap.product.domain.model;

/**
 * Value object representing a product identifier.
 * Immutable and self-validating.
 */
public record ProductId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the product ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public ProductId {
        if (value == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
    }
}
