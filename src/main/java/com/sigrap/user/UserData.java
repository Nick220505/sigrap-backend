package com.sigrap.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user creation and updates.
 * Contains user data submitted in API requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User creation/update data")
public class UserData {

  /**
   * Full name of the user.
   * Must not be blank.
   */
  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must be less than 100 characters")
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  /**
   * Email address of the user.
   * Must be a valid email format and unique in the system.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Schema(
    description = "User's email address",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Password for the user account.
   * Required for new users, optional for updates.
   */
  @Schema(description = "User's password", example = "SecureP@ssw0rd")
  private String password;

  /**
   * Phone number of the user.
   */
  @Schema(description = "User's phone number", example = "+1234567890")
  private String phone;

  /**
   * Account status of the user.
   */
  @Schema(description = "User's account status", example = "ACTIVE")
  private UserStatus status;

  /**
   * Role of the user.
   */
  @Schema(description = "User's role", example = "EMPLOYEE")
  private UserRole role;
}
