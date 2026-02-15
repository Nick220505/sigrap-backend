package com.sigrap.user.domain.model;

/**
 * Value object representing a user identifier.
 * Immutable and self-validating.
 */
public record UserId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the user ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }
}
