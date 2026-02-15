package com.sigrap.supplier.domain.model;

import java.util.regex.Pattern;

/**
 * Value object representing a supplier email address.
 * Immutable and self-validating.
 */
public record SupplierEmail(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    /**
     * Compact constructor with validation.
     * Ensures the email address is valid (proper format and within length limits).
     *
     * @throws IllegalArgumentException if the value is null, invalid format, or exceeds maximum length
     */
    public SupplierEmail {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Supplier email cannot be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Supplier email cannot exceed 100 characters");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Supplier email must be a valid email address");
        }
    }
}
