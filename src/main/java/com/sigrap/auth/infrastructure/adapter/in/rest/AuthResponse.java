package com.sigrap.auth.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response DTO for authentication operations.
 * Contains the JWT token and user information after successful authentication or registration.
 */
@Schema(
  description = "Authentication response containing JWT token and user details",
  example = """
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresAt": "2026-02-16T21:00:00",
      "email": "user@example.com",
      "name": "John Doe",
      "authenticatedAt": "2026-02-15T21:00:00",
      "role": "USER"
    }
    """
)
public record AuthResponse(
  @Schema(
    description = "JWT token - include this in the Authorization header as 'Bearer {token}' for authenticated requests",
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA4MDM2ODAwLCJleHAiOjE3MDgxMjMyMDB9.signature"
  )
  String token,
  
  @Schema(
    description = "Token expiration timestamp - after this time, the token will no longer be valid",
    example = "2026-02-16T21:00:00"
  )
  LocalDateTime expiresAt,
  
  @Schema(
    description = "User email address",
    example = "user@example.com"
  )
  String email,
  
  @Schema(
    description = "User full name",
    example = "John Doe"
  )
  String name,
  
  @Schema(
    description = "Timestamp when the authentication occurred",
    example = "2026-02-15T21:00:00"
  )
  LocalDateTime authenticatedAt,
  
  @Schema(
    description = "User role in the system",
    example = "USER"
  )
  String role
) {}
