package com.sigrap.sale.domain.model;

/**
 * Value object representing a sale return number.
 * Immutable and self-validating.
 * Sale return numbers follow a specific format for tracking and reference.
 */
public record SaleReturnNumber(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the sale return number is valid and follows business rules.
     *
     * @throws IllegalArgumentException if the value is null, blank, or invalid format
     */
    public SaleReturnNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sale return number cannot be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Sale return number cannot exceed 50 characters");
        }
    }
}
