package com.sigrap.category.domain.model;

/**
 * Value object representing a category identifier.
 * Immutable and self-validating.
 */
public record CategoryId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the category ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public CategoryId {
        if (value == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Category ID must be positive");
        }
    }
}
