package com.sigrap.user.domain.model;

/**
 * Value object representing a permission name.
 * Immutable and self-validating.
 */
public record PermissionName(String value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the permission name is valid (not blank, within length limits).
     *
     * @throws IllegalArgumentException if the value is null, blank, or exceeds length limits
     */
    public PermissionName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Permission name cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Permission name cannot exceed 100 characters");
        }
    }
}
