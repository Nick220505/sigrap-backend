package com.sigrap.category.domain.model;

/**
 * Value object representing a category name.
 * Immutable and self-validating.
 */
public record CategoryName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the category name is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
    }
}
