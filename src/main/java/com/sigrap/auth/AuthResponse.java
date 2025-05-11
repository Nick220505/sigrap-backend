package com.sigrap.auth;

import java.time.LocalDateTime;

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

  /**
   * JWT authentication token generated upon successful authentication.
   * Used by clients for authorizing subsequent API requests.
   * Should be included in the Authorization header as "Bearer {token}".
   */
  @Schema(
    description = "JWT authentication token",
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  )
  private String token;

  /**
   * Email address of the authenticated user.
   * Serves as the unique identifier for the user in the system.
   * Returned to confirm which user has been authenticated.
   */
  @Schema(description = "User's email address", example = "user@example.com")
  private String email;

  /**
   * Full name of the authenticated user.
   * Used for display purposes in the client application.
   * Provides a human-readable identifier for the authenticated user.
   */
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  /**
   * Timestamp of the user's last successful login.
   * Updated each time the user successfully authenticates.
   */
  @Schema(description = "Last login timestamp", example = "2024-01-01T10:30:00")
  private LocalDateTime lastLogin;
}
