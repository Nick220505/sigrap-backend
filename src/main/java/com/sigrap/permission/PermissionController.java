package com.sigrap.permission;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for permission management.
 * Provides endpoints for creating, retrieving, updating, and deleting permissions,
 * as well as managing permission-role relationships.
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management API")
public class PermissionController {

  private final PermissionService permissionService;

  /**
   * Retrieves all permissions or filters by resource.
   *
   * @param resource Optional resource name to filter by
   * @return List of PermissionInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get permissions",
    description = "Retrieves a list of all permissions or filters by resource name"
  )
  public List<PermissionInfo> getPermissions(
    @RequestParam(required = false) String resource
  ) {
    if (resource != null && !resource.isEmpty()) {
      return permissionService.findByResource(resource);
    }
    return permissionService.findAll();
  }

  /**
   * Retrieves a permission by ID.
   *
   * @param id The ID of the permission to retrieve
   * @return PermissionInfo DTO
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get permission by ID",
    description = "Retrieves a specific permission by its unique identifier"
  )
  public PermissionInfo getPermissionById(@PathVariable Long id) {
    return permissionService.findById(id);
  }

  /**
   * Creates a new permission.
   *
   * @param permissionData The data for the new permission
   * @return PermissionInfo DTO containing the created permission
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create permission",
    description = "Creates a new permission with the provided data"
  )
  public PermissionInfo createPermission(
    @Valid @RequestBody PermissionData permissionData
  ) {
    return permissionService.create(permissionData);
  }

  /**
   * Updates an existing permission.
   *
   * @param id The ID of the permission to update
   * @param permissionData The new data for the permission
   * @return PermissionInfo DTO containing the updated permission
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update permission",
    description = "Updates an existing permission with the provided data"
  )
  public PermissionInfo updatePermission(
    @PathVariable Long id,
    @Valid @RequestBody PermissionData permissionData
  ) {
    return permissionService.update(id, permissionData);
  }

  /**
   * Deletes a permission.
   *
   * @param id The ID of the permission to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete permission",
    description = "Deletes a permission by its ID"
  )
  public void deletePermission(@PathVariable Long id) {
    permissionService.delete(id);
  }

  /**
   * Assigns a permission to a role.
   *
   * @param id The ID of the permission
   * @param roleId The ID of the role
   * @return PermissionInfo DTO containing the permission's data
   */
  @PostMapping("/{id}/roles/{roleId}")
  @Operation(
    summary = "Assign permission to role",
    description = "Assigns a specified permission to a role"
  )
  public PermissionInfo assignPermissionToRole(
    @PathVariable Long id,
    @PathVariable Long roleId
  ) {
    return permissionService.assignToRole(id, roleId);
  }

  /**
   * Removes a permission from a role.
   *
   * @param id The ID of the permission
   * @param roleId The ID of the role
   */
  @DeleteMapping("/{id}/roles/{roleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Remove permission from role",
    description = "Removes a specified permission from a role"
  )
  public void removePermissionFromRole(
    @PathVariable Long id,
    @PathVariable Long roleId
  ) {
    permissionService.removeFromRole(id, roleId);
  }
}
