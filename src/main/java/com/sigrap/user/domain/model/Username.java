package com.sigrap.user.domain.model;

/**
 * Value object representing a username.
 * Immutable and self-validating.
 */
public record Username(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the username is valid (not blank, within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds length limits
     */
    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (value.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Username cannot exceed 100 characters");
        }
    }
}
