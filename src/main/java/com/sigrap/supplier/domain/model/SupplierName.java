package com.sigrap.supplier.domain.model;

/**
 * Value object representing a supplier name.
 * Immutable and self-validating.
 */
public record SupplierName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the supplier name is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public SupplierName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Supplier name cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Supplier name cannot exceed 100 characters");
        }
    }
}
