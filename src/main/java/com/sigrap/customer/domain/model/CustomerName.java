package com.sigrap.customer.domain.model;

/**
 * Value object representing a customer name.
 * Immutable and self-validating.
 */
public record CustomerName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the customer name is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public CustomerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Customer name cannot exceed 255 characters");
        }
    }
}
