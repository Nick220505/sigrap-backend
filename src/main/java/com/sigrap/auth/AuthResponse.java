package com.sigrap.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for authentication responses.
 * Contains the authentication result data returned to clients.
 *
 * <p>This class represents the successful authentication response,
 * including the JWT token for subsequent authenticated requests
 * and basic user information.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model for successful authentication")
public class AuthResponse {

  @Schema(
    description = "JWT authentication token",
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  )
  private String token;

  @Schema(description = "User's email address", example = "user@example.com")
  private String email;

  @Schema(description = "User's full name", example = "John Doe")
  private String name;
}
