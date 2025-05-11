package com.sigrap.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating a password reset.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;
}
