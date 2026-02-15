package com.sigrap.sale.domain.model;

/**
 * Value object representing a sale identifier.
 * Immutable and self-validating.
 */
public record SaleId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the sale ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public SaleId {
        if (value == null) {
            throw new IllegalArgumentException("Sale ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Sale ID must be positive");
        }
    }
}
