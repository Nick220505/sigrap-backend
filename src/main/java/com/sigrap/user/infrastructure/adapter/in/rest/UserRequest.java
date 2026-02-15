package com.sigrap.user.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param username the username (required for creation, max 50 characters)
 * @param email the email address (required, must be valid email format, max 100 characters)
 * @param password the plain text password (required for creation, min 8 characters)
 */
public record UserRequest(
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}
