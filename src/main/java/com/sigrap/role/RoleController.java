package com.sigrap.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for role management.
 * Provides endpoints for creating, retrieving, updating, and deleting roles,
 * as well as managing role-user relationships.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management API")
public class RoleController {

  private final RoleService roleService;

  /**
   * Retrieves all roles.
   *
   * @return List of RoleInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all roles",
    description = "Retrieves a list of all roles in the system"
  )
  public List<RoleInfo> getAllRoles() {
    return roleService.findAll();
  }

  /**
   * Retrieves a role by ID.
   *
   * @param id The ID of the role to retrieve
   * @return RoleInfo DTO
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get role by ID",
    description = "Retrieves a specific role by its unique identifier"
  )
  public RoleInfo getRoleById(@PathVariable Long id) {
    return roleService.findById(id);
  }

  /**
   * Creates a new role.
   *
   * @param roleData The data for the new role
   * @return RoleInfo DTO containing the created role
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create role",
    description = "Creates a new role with the provided data"
  )
  public RoleInfo createRole(@Valid @RequestBody RoleData roleData) {
    return roleService.create(roleData);
  }

  /**
   * Updates an existing role.
   *
   * @param id The ID of the role to update
   * @param roleData The new data for the role
   * @return RoleInfo DTO containing the updated role
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update role",
    description = "Updates an existing role with the provided data"
  )
  public RoleInfo updateRole(
    @PathVariable Long id,
    @Valid @RequestBody RoleData roleData
  ) {
    return roleService.update(id, roleData);
  }

  /**
   * Deletes a role.
   *
   * @param id The ID of the role to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete role", description = "Deletes a role by its ID")
  public void deleteRole(@PathVariable Long id) {
    roleService.delete(id);
  }

  /**
   * Assigns a role to a user.
   *
   * @param id The ID of the role
   * @param userId The ID of the user
   * @return RoleInfo DTO containing the role's data
   */
  @PostMapping("/{id}/users/{userId}")
  @Operation(
    summary = "Assign role to user",
    description = "Assigns a specified role to a user"
  )
  public RoleInfo assignRoleToUser(
    @PathVariable Long id,
    @PathVariable Long userId
  ) {
    return roleService.assignToUser(id, userId);
  }

  /**
   * Removes a role from a user.
   *
   * @param id The ID of the role
   * @param userId The ID of the user
   */
  @DeleteMapping("/{id}/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Remove role from user",
    description = "Removes a specified role from a user"
  )
  public void removeRoleFromUser(
    @PathVariable Long id,
    @PathVariable Long userId
  ) {
    roleService.removeFromUser(id, userId);
  }
}
