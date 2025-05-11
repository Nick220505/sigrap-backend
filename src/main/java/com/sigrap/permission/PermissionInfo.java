package com.sigrap.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for permission information.
 * Contains permission data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Permission information")
public class PermissionInfo {

  /**
   * Unique identifier for the permission.
   */
  @Schema(description = "Permission ID", example = "1")
  private Long id;

  /**
   * The name of the permission.
   */
  @Schema(description = "Permission name", example = "USER_CREATE")
  private String name;

  /**
   * The description of the permission.
   */
  @Schema(
    description = "Description of the permission",
    example = "Allows creating new user accounts"
  )
  private String description;

  /**
   * The resource that this permission applies to.
   */
  @Schema(description = "Resource the permission applies to", example = "USER")
  private String resource;

  /**
   * The action that this permission allows on the resource.
   */
  @Schema(description = "Action allowed by this permission", example = "CREATE")
  private String action;

  /**
   * Timestamp when the permission was created.
   */
  @Schema(
    description = "Timestamp when the permission was created",
    example = "2023-06-15T10:30:45"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp when the permission was last updated.
   */
  @Schema(
    description = "Timestamp when the permission was last updated",
    example = "2023-06-16T08:15:30"
  )
  private LocalDateTime updatedAt;
}
