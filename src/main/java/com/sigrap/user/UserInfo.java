package com.sigrap.user;

import com.sigrap.role.RoleInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user information.
 * Contains user data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information")
public class UserInfo {

  /**
   * Unique identifier for the user.
   */
  @Schema(description = "User ID", example = "1")
  private Long id;

  /**
   * Full name of the user.
   */
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  /**
   * Email address of the user.
   */
  @Schema(
    description = "User's email address",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Phone number of the user.
   */
  @Schema(description = "User's phone number", example = "+1234567890")
  private String phone;

  /**
   * Current status of the user's account.
   */
  @Schema(description = "User's account status", example = "ACTIVE")
  private User.UserStatus status;

  /**
   * Timestamp of the user's last successful login.
   */
  @Schema(description = "Last login timestamp", example = "2023-04-15T10:30:00")
  private LocalDateTime lastLogin;

  /**
   * Collection of roles assigned to this user.
   */
  @Schema(description = "User roles")
  private Set<RoleInfo> roles;
}
