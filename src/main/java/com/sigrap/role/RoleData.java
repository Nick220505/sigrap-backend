package com.sigrap.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating roles.
 * Contains the data needed for role creation and modification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a role")
public class RoleData {

  /**
   * The name of the role.
   * Must not be blank and has a maximum length of 50 characters.
   */
  @NotBlank(message = "Role name is required")
  @Size(max = 50, message = "Role name must not exceed 50 characters")
  @Schema(description = "Name of the role", example = "ADMIN")
  private String name;

  /**
   * The description of the role.
   * Has a maximum length of 500 characters.
   */
  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(
    description = "Description of the role's purpose",
    example = "Administrators with full system access"
  )
  private String description;

  /**
   * The IDs of permissions to assign to this role.
   */
  @Schema(description = "IDs of permissions to assign to this role")
  private Set<Long> permissionIds;
}
