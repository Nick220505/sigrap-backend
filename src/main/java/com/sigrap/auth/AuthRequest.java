package com.sigrap.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for authentication requests.
 * Contains the credentials needed for user authentication.
 *
 * <p>This class is used to transfer login credentials from clients
 * to the authentication endpoint. It includes validation to ensure
 * required fields are provided and properly formatted.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for user authentication")
public class AuthRequest {

  /**
   * The user's email address used for authentication.
   * Serves as the username in the authentication process.
   * Must be a valid email format and cannot be blank.
   */
  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  @Schema(description = "User's email address", example = "user@example.com")
  private String email;

  /**
   * The user's password for authentication.
   * Must not be blank and is validated against the stored encrypted password.
   * Never returned in responses for security reasons.
   */
  @NotBlank(message = "Password cannot be empty")
  @Schema(description = "User's password", example = "Password123!")
  private String password;
}
