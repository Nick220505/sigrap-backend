package com.sigrap.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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
   * Timestamp of the user's last successful login.
   */
  @Schema(description = "Last login timestamp", example = "2023-04-15T10:30:00")
  private LocalDateTime lastLogin;

  /**
   * Role assigned to this user.
   */
  @Schema(description = "User role", example = "EMPLOYEE")
  private UserRole role;

  @Schema(description = "Document ID of the user", example = "123456789")
  private String documentId;

  @Schema(description = "Creation timestamp", example = "2023-01-15T09:00:00")
  private LocalDateTime createdAt;

  @Schema(
    description = "Last update timestamp",
    example = "2023-01-15T09:30:00"
  )
  private LocalDateTime updatedAt;
}
