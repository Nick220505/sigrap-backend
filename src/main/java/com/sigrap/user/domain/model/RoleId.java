package com.sigrap.user.domain.model;

/**
 * Value object representing a role identifier.
 * Immutable and self-validating.
 */
public record RoleId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the role ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public RoleId {
        if (value == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Role ID must be positive");
        }
    }
}
