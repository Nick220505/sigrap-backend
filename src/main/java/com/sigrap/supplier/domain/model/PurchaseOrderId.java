package com.sigrap.supplier.domain.model;

/**
 * Value object representing a purchase order identifier.
 * Immutable and self-validating.
 */
public record PurchaseOrderId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the purchase order ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public PurchaseOrderId {
        if (value == null) {
            throw new IllegalArgumentException("Purchase order ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Purchase order ID must be positive");
        }
    }
}
