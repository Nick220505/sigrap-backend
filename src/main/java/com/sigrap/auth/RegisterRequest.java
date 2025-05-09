package com.sigrap.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * Contains validated user registration data.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates registration input data</li>
 *   <li>Enforces password security rules</li>
 *   <li>Ensures data completeness</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Name must not be blank</li>
 *   <li>Email must be valid format</li>
 *   <li>Password must meet security requirements:
 *     <ul>
 *       <li>Minimum 8 characters</li>
 *       <li>At least one uppercase letter</li>
 *       <li>At least one lowercase letter</li>
 *       <li>At least one number</li>
 *       <li>At least one special character (@$!%*?&amp;)</li>
 *     </ul>
 *   </li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * RegisterRequest request = RegisterRequest.builder()
 *     .name("John Doe")
 *     .email("john@example.com")
 *     .password("SecurePass123!")
 *     .build();
 * </pre></p>
 *
 * @see AuthController
 * @see AuthService
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for user registration")
public class RegisterRequest {

  /**
   * The full name of the user.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be blank</li>
   *   <li>Should be the user's complete name</li>
   * </ul></p>
   */
  @NotBlank(message = "Name cannot be empty")
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  /**
   * The email address of the user.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be blank</li>
   *   <li>Must be a valid email format</li>
   *   <li>Will be used as the username for login</li>
   *   <li>Must be unique in the system</li>
   * </ul></p>
   */
  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  @Schema(description = "User's email address", example = "user@example.com")
  private String email;

  /**
   * The password for the user account.
   *
   * <p>Security Requirements:
   * <ul>
   *   <li>Minimum 8 characters</li>
   *   <li>At least one uppercase letter</li>
   *   <li>At least one lowercase letter</li>
   *   <li>At least one number</li>
   *   <li>At least one special character (@$!%*?&amp;)</li>
   * </ul></p>
   *
   * <p>The password will be:
   * <ul>
   *   <li>Validated against security rules</li>
   *   <li>Encrypted before storage</li>
   *   <li>Never returned in responses</li>
   * </ul></p>
   */
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
