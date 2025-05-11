package com.sigrap.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for confirming a password reset.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetConfirmRequest {

  @NotBlank(message = "Token is required")
  private String token;

  @NotBlank(message = "New password is required")
  private String newPassword;
}
