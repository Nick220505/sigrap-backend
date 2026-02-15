package com.sigrap.user.domain.model;

/**
 * Value object representing a permission identifier.
 * Immutable and self-validating.
 */
public record PermissionId(Long value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the permission ID is valid (positive number).
     *
     * @throws IllegalArgumentException if the value is null or not positive
     */
    public PermissionId {
        if (value == null) {
            throw new IllegalArgumentException("Permission ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Permission ID must be positive");
        }
    }
}
