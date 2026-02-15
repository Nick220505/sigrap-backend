package com.sigrap.customer.domain.model;

/**
 * Value object representing a customer phone number.
 * Immutable and self-validating.
 */
public record CustomerPhone(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the phone number is valid (not blank and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds maximum length
     */
    public CustomerPhone {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer phone cannot be blank");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("Customer phone cannot exceed 20 characters");
        }
    }
}
