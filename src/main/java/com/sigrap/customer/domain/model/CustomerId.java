package com.sigrap.customer.domain.model;

/**
 * Value object representing a customer identifier.
 * Immutable and self-validating.
 */
public record CustomerId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the customer ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public CustomerId {
        if (value == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive");
        }
    }
}
