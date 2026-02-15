package com.sigrap.user.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user information.
 * All fields are optional - only provided fields will be updated.
 *
 * @param email the new email address (optional, must be valid email format if provided)
 * @param password the new plain text password (optional, min 8 characters if provided)
 */
public record UpdateUserRequest(
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}
