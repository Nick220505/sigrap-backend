package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
    description = "Request body for creating a new user account",
    example = """
        {
            "username": "johndoe",
            "email": "johndoe@example.com",
            "password": "SecurePass123!"
        }
        """
)
public record UserRequest(
    @Schema(
        description = "Unique username for the user account - must be unique across the system",
        example = "johndoe",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    String username,
    
    @Schema(
        description = "User's email address - must be valid email format and unique",
        example = "johndoe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,
    
    @Schema(
        description = "User's password - minimum 8 characters for security",
        example = "SecurePass123!",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8
    )
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}
