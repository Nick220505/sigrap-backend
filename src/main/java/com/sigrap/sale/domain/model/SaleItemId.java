package com.sigrap.sale.domain.model;

/**
 * Value object representing a sale item identifier.
 * Immutable and self-validating.
 */
public record SaleItemId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the sale item ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public SaleItemId {
        if (value == null) {
            throw new IllegalArgumentException("Sale item ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Sale item ID must be positive");
        }
    }
}
