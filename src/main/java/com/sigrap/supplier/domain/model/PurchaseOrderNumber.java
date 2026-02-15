package com.sigrap.supplier.domain.model;

/**
 * Value object representing a purchase order number.
 * Immutable and self-validating.
 */
public record PurchaseOrderNumber(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the purchase order number is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public PurchaseOrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Purchase order number cannot be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Purchase order number cannot exceed 50 characters");
        }
    }
}
