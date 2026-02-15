package com.sigrap.sale.domain.model;

/**
 * Value object representing a sale number.
 * Immutable and self-validating.
 * Sale numbers follow a specific format for tracking and reference.
 */
public record SaleNumber(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the sale number is valid and follows business rules.
     *
     * @throws IllegalArgumentException if the value is null, blank, or invalid format
     */
    public SaleNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sale number cannot be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Sale number cannot exceed 50 characters");
        }
    }
}
