package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreateRoleCommand;
import com.sigrap.user.application.port.in.command.UpdateRoleCommand;
import com.sigrap.user.domain.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for role operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "User Management", description = "APIs for managing users, roles, and permissions")
public class RoleController {
    
    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final AssignPermissionUseCase assignPermissionUseCase;
    private final RemovePermissionUseCase removePermissionUseCase;
    private final RoleResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     */
    public RoleController(
            CreateRoleUseCase createRoleUseCase,
            GetRoleUseCase getRoleUseCase,
            UpdateRoleUseCase updateRoleUseCase,
            DeleteRoleUseCase deleteRoleUseCase,
            AssignPermissionUseCase assignPermissionUseCase,
            RemovePermissionUseCase removePermissionUseCase,
            RoleResponseMapper responseMapper) {
        this.createRoleUseCase = createRoleUseCase;
        this.getRoleUseCase = getRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.assignPermissionUseCase = assignPermissionUseCase;
        this.removePermissionUseCase = removePermissionUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new role.
     * POST /api/roles
     *
     * @param request the role creation request
     * @return the created role response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new role",
        description = "Creates a new role with the provided name and description. " +
                      "The role name must be unique across the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Role created successfully",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate role name"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        CreateRoleCommand command = new CreateRoleCommand(
            request.name(),
            request.description()
        );
        Role role = createRoleUseCase.create(command);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Retrieves a role by its ID.
     * GET /api/roles/{id}
     *
     * @param id the role identifier
     * @return the role response with HTTP 200 status
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get role by ID",
        description = "Retrieves a role by its unique identifier."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Role found",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse getById(
        @Parameter(description = "Role unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        Role role = getRoleUseCase.getById(id);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Retrieves a role by name.
     * GET /api/roles/name/{name}
     *
     * @param name the role name
     * @return the role response with HTTP 200 status
     */
    @GetMapping("/name/{name}")
    @Operation(
        summary = "Get role by name",
        description = "Retrieves a role by its unique name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Role found",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse getByName(
        @Parameter(description = "Role name", required = true, example = "ADMIN")
        @PathVariable String name
    ) {
        Role role = getRoleUseCase.getByName(name);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Retrieves all roles.
     * GET /api/roles
     *
     * @return a list of all role responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all roles",
        description = "Retrieves a list of all roles in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of roles retrieved successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<RoleResponse> getAll() {
        return getRoleUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing role.
     * PUT /api/roles/{id}
     *
     * @param id the role identifier
     * @param request the role update request
     * @return the updated role response with HTTP 200 status
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing role",
        description = "Updates an existing role with the provided name and description."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Role updated successfully",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse update(
            @Parameter(description = "Role unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        UpdateRoleCommand command = new UpdateRoleCommand(
            request.name(),
            request.description()
        );
        Role role = updateRoleUseCase.update(id, command);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Assigns a permission to a role.
     * POST /api/roles/{roleId}/permissions/{permissionId}
     *
     * @param roleId the role identifier
     * @param permissionId the permission identifier
     * @return the updated role response with HTTP 200 status
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @Operation(
        summary = "Assign a permission to a role",
        description = "Assigns a specific permission to a role, granting that permission to all users with this role."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Permission assigned successfully",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role or permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse assignPermission(
            @Parameter(description = "Role unique identifier", required = true, example = "1")
            @PathVariable Long roleId,
            @Parameter(description = "Permission unique identifier", required = true, example = "5")
            @PathVariable Long permissionId) {
        Role role = assignPermissionUseCase.assignPermission(roleId, permissionId);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Removes a permission from a role.
     * DELETE /api/roles/{roleId}/permissions/{permissionId}
     *
     * @param roleId the role identifier
     * @param permissionId the permission identifier
     * @return the updated role response with HTTP 200 status
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(
        summary = "Remove a permission from a role",
        description = "Removes a specific permission from a role, revoking that permission from all users with this role."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Permission removed successfully",
        content = @Content(schema = @Schema(implementation = RoleResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role or permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public RoleResponse removePermission(
            @Parameter(description = "Role unique identifier", required = true, example = "1")
            @PathVariable Long roleId,
            @Parameter(description = "Permission unique identifier", required = true, example = "5")
            @PathVariable Long permissionId) {
        Role role = removePermissionUseCase.removePermission(roleId, permissionId);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Deletes a role by its ID.
     * DELETE /api/roles/{id}
     *
     * @param id the role identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a role",
        description = "Permanently deletes a role from the system."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Role deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void delete(
        @Parameter(description = "Role unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        deleteRoleUseCase.delete(id);
    }
}
