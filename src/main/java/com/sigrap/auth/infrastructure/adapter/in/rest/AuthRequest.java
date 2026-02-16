package com.sigrap.auth.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user authentication.
 * Contains the credentials needed to authenticate a user.
 */
@Schema(
  description = "Request body for user authentication (login)",
  example = """
    {
      "email": "user@example.com",
      "password": "SecurePass123"
    }
    """
)
public record AuthRequest(
  @Schema(
    description = "User email address - must be a valid email format",
    example = "user@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  String email,

  @Schema(
    description = "User password - minimum 8 characters required",
    example = "SecurePass123",
    requiredMode = Schema.RequiredMode.REQUIRED,
    minLength = 8
  )
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  String password
) {}
