package com.sigrap.supplier.domain.model;

import java.util.regex.Pattern;

/**
 * Value object representing a supplier phone number.
 * Immutable and self-validating.
 */
public record SupplierPhone(String value) {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]*$");
    
    /**
     * Compact constructor with validation.
     * Ensures the phone number is valid (proper format and within length limits).
     *
     * @throws IllegalArgumentException if the value is invalid format or exceeds maximum length
     */
    public SupplierPhone {
        if (value != null) {
            if (value.length() > 20) {
                throw new IllegalArgumentException("Supplier phone cannot exceed 20 characters");
            }
            if (!PHONE_PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("Supplier phone contains invalid characters");
            }
        }
    }
}
