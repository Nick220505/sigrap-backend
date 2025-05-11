package com.sigrap.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating permissions.
 * Contains the data needed for permission creation and modification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a permission")
public class PermissionData {

  /**
   * The name of the permission.
   * Must not be blank and has a maximum length of 50 characters.
   */
  @NotBlank(message = "Permission name is required")
  @Size(max = 50, message = "Permission name must not exceed 50 characters")
  @Schema(description = "Name of the permission", example = "USER_CREATE")
  private String name;

  /**
   * The description of the permission.
   * Has a maximum length of 500 characters.
   */
  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(
    description = "Description of the permission's purpose",
    example = "Allows creating new user accounts"
  )
  private String description;

  /**
   * The resource that this permission applies to.
   * Must not be blank.
   */
  @NotBlank(message = "Resource is required")
  @Schema(description = "Resource the permission applies to", example = "USER")
  private String resource;

  /**
   * The action that this permission allows on the resource.
   * Must not be blank.
   */
  @NotBlank(message = "Action is required")
  @Schema(description = "Action allowed by this permission", example = "CREATE")
  private String action;
}
