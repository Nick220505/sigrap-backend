package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreatePermissionCommand;
import com.sigrap.user.application.port.in.command.UpdatePermissionCommand;
import com.sigrap.user.domain.model.Permission;
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
 * REST controller for permission operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "User Management", description = "APIs for managing users, roles, and permissions")
public class PermissionController {
    
    private final CreatePermissionUseCase createPermissionUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final PermissionResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     */
    public PermissionController(
            CreatePermissionUseCase createPermissionUseCase,
            GetPermissionUseCase getPermissionUseCase,
            UpdatePermissionUseCase updatePermissionUseCase,
            DeletePermissionUseCase deletePermissionUseCase,
            PermissionResponseMapper responseMapper) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.getPermissionUseCase = getPermissionUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
        this.deletePermissionUseCase = deletePermissionUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new permission.
     * POST /api/permissions
     *
     * @param request the permission creation request
     * @return the created permission response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new permission",
        description = "Creates a new permission with the provided name, resource, and action. " +
                      "The permission name must be unique across the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Permission created successfully",
        content = @Content(schema = @Schema(implementation = PermissionResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate permission name"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public PermissionResponse create(@Valid @RequestBody PermissionRequest request) {
        CreatePermissionCommand command = new CreatePermissionCommand(
            request.name(),
            request.resource(),
            request.action()
        );
        Permission permission = createPermissionUseCase.create(command);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Retrieves a permission by its ID.
     * GET /api/permissions/{id}
     *
     * @param id the permission identifier
     * @return the permission response with HTTP 200 status
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get permission by ID",
        description = "Retrieves a permission by its unique identifier."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Permission found",
        content = @Content(schema = @Schema(implementation = PermissionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public PermissionResponse getById(
        @Parameter(description = "Permission unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        Permission permission = getPermissionUseCase.getById(id);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Retrieves a permission by name.
     * GET /api/permissions/name/{name}
     *
     * @param name the permission name
     * @return the permission response with HTTP 200 status
     */
    @GetMapping("/name/{name}")
    @Operation(
        summary = "Get permission by name",
        description = "Retrieves a permission by its unique name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Permission found",
        content = @Content(schema = @Schema(implementation = PermissionResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public PermissionResponse getByName(
        @Parameter(description = "Permission name", required = true, example = "USER_READ")
        @PathVariable String name
    ) {
        Permission permission = getPermissionUseCase.getByName(name);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Retrieves all permissions for a specific resource.
     * GET /api/permissions/resource/{resource}
     *
     * @param resource the resource name
     * @return a list of permission responses with HTTP 200 status
     */
    @GetMapping("/resource/{resource}")
    @Operation(
        summary = "Get permissions by resource",
        description = "Retrieves all permissions associated with a specific resource."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of permissions for the resource retrieved successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<PermissionResponse> getByResource(
        @Parameter(description = "Resource name", required = true, example = "USER")
        @PathVariable String resource
    ) {
        return getPermissionUseCase.getByResource(resource).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all permissions.
     * GET /api/permissions
     *
     * @return a list of all permission responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all permissions",
        description = "Retrieves a list of all permissions in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of permissions retrieved successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<PermissionResponse> getAll() {
        return getPermissionUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing permission.
     * PUT /api/permissions/{id}
     *
     * @param id the permission identifier
     * @param request the permission update request
     * @return the updated permission response with HTTP 200 status
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing permission",
        description = "Updates an existing permission with the provided name, resource, and action."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Permission updated successfully",
        content = @Content(schema = @Schema(implementation = PermissionResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public PermissionResponse update(
            @Parameter(description = "Permission unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        UpdatePermissionCommand command = new UpdatePermissionCommand(
            request.name(),
            request.resource(),
            request.action()
        );
        Permission permission = updatePermissionUseCase.update(id, command);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Deletes a permission by its ID.
     * DELETE /api/permissions/{id}
     *
     * @param id the permission identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a permission",
        description = "Permanently deletes a permission from the system."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Permission deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Permission not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void delete(
        @Parameter(description = "Permission unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        deletePermissionUseCase.delete(id);
    }
}
