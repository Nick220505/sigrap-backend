package com.sigrap.auth.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration.
 * Contains the data needed to register a new user.
 */
@Schema(
  description = "Request body for user registration",
  example = """
    {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "SecurePass123"
    }
    """
)
public record RegisterRequest(
  @Schema(
    description = "User full name - must be between 2 and 50 characters",
    example = "John Doe",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minLength = 2,
    maxLength = 50
  )
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  String name,

  @Schema(
    description = "User email address - must be unique and in valid email format",
    example = "john.doe@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  String email,

  @Schema(
    description = "User password - minimum 8 characters required for security",
    example = "SecurePass123",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minLength = 8
  )
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  String password
) {}
