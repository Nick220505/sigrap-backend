package com.sigrap.role;

import com.sigrap.permission.PermissionInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for role information.
 * Contains role data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Role information")
public class RoleInfo {

  /**
   * Unique identifier for the role.
   */
  @Schema(description = "Role ID", example = "1")
  private Long id;

  /**
   * The name of the role.
   */
  @Schema(description = "Role name", example = "ADMIN")
  private String name;

  /**
   * The description of the role.
   */
  @Schema(
    description = "Description of the role",
    example = "Administrators with full system access"
  )
  private String description;

  /**
   * The permissions assigned to this role.
   */
  @Schema(description = "Permissions assigned to this role")
  private Set<PermissionInfo> permissions;

  /**
   * Timestamp when the role was created.
   */
  @Schema(
    description = "Timestamp when the role was created",
    example = "2023-06-15T10:30:45"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp when the role was last updated.
   */
  @Schema(
    description = "Timestamp when the role was last updated",
    example = "2023-06-16T08:15:30"
  )
  private LocalDateTime updatedAt;
}
