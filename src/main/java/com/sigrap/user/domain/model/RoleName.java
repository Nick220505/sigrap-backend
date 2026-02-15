package com.sigrap.user.domain.model;

/**
 * Value object representing a role name.
 * Immutable and self-validating.
 */
public record RoleName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the role name is valid (not blank, within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds length limits
     */
    public RoleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Role name cannot exceed 50 characters");
        }
    }
}
