package com.sigrap.product.domain.model;

/**
 * Value object representing a product name.
 * Immutable and self-validating.
 */
public record ProductName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the product name is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Product name cannot exceed 255 characters");
        }
    }
}
