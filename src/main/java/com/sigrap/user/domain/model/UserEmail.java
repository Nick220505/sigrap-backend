package com.sigrap.user.domain.model;

import java.util.regex.Pattern;

/**
 * Value object representing a user email address.
 * Immutable and self-validating.
 */
public record UserEmail(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    /**
     * Compact constructor with validation.
     * Ensures the email is valid (not blank, valid format).
     *
     * @throws IllegalArgumentException if the value is null, blank, or invalid format
     */
    public UserEmail {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters");
        }
    }
}
