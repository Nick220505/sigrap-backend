package com.sigrap.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for user authentication")
public class AuthRequest {

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  @Schema(description = "User's email address", example = "user@example.com", required = true)
  private String email;

  @NotBlank(message = "Password cannot be empty")
  @Schema(description = "User's password", example = "Password123!", required = true)
  private String password;
}