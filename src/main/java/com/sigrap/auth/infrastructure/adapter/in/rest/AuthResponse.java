package com.sigrap.auth.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

/**
 * Response DTO for authentication operations.
 * Contains the JWT token and user information after successful authentication or registration.
 */
public record AuthResponse(
  String token,
  LocalDateTime expiresAt,
  String email,
  String name,
  LocalDateTime authenticatedAt,
  String role
) {}
