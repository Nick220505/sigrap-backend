package com.sigrap.supplier.domain.model;

/**
 * Value object representing a supplier identifier.
 * Immutable and self-validating.
 */
public record SupplierId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the supplier ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public SupplierId {
        if (value == null) {
            throw new IllegalArgumentException("Supplier ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
    }
}
