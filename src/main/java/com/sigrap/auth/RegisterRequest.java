package com.sigrap.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for user registration")
public class RegisterRequest {

  @NotBlank(message = "Name cannot be empty")
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  @Schema(description = "User's email address", example = "user@example.com")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must be at least 8 characters long and contain at least one uppercase letter," +
    " one lowercase letter, one number, and one special character"
  )
  @Schema(
    description = "User's password - must be at least 8 characters with uppercase, lowercase, number and" +
    " special character",
    example = "Password123!"
  )
  private String password;
}
