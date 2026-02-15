package com.sigrap.auth.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user authentication.
 * Contains the credentials needed to authenticate a user.
 */
public record AuthRequest(
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  String email,

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  String password
) {}
