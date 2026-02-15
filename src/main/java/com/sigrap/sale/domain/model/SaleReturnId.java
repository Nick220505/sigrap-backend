package com.sigrap.sale.domain.model;

/**
 * Value object representing a sale return identifier.
 * Immutable and self-validating.
 */
public record SaleReturnId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the sale return ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public SaleReturnId {
        if (value == null) {
            throw new IllegalArgumentException("Sale return ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Sale return ID must be positive");
        }
    }
}
