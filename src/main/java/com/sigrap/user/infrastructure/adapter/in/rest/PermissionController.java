package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreatePermissionCommand;
import com.sigrap.user.application.port.in.command.UpdatePermissionCommand;
import com.sigrap.user.domain.model.Permission;
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
@RequestMapping("/api/v2/permissions")
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
     * POST /api/v2/permissions
     *
     * @param request the permission creation request
     * @return the created permission response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
     * GET /api/v2/permissions/{id}
     *
     * @param id the permission identifier
     * @return the permission response with HTTP 200 status
     */
    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable Long id) {
        Permission permission = getPermissionUseCase.getById(id);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Retrieves a permission by name.
     * GET /api/v2/permissions/name/{name}
     *
     * @param name the permission name
     * @return the permission response with HTTP 200 status
     */
    @GetMapping("/name/{name}")
    public PermissionResponse getByName(@PathVariable String name) {
        Permission permission = getPermissionUseCase.getByName(name);
        return responseMapper.toResponse(permission);
    }
    
    /**
     * Retrieves all permissions for a specific resource.
     * GET /api/v2/permissions/resource/{resource}
     *
     * @param resource the resource name
     * @return a list of permission responses with HTTP 200 status
     */
    @GetMapping("/resource/{resource}")
    public List<PermissionResponse> getByResource(@PathVariable String resource) {
        return getPermissionUseCase.getByResource(resource).stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all permissions.
     * GET /api/v2/permissions
     *
     * @return a list of all permission responses with HTTP 200 status
     */
    @GetMapping
    public List<PermissionResponse> getAll() {
        return getPermissionUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing permission.
     * PUT /api/v2/permissions/{id}
     *
     * @param id the permission identifier
     * @param request the permission update request
     * @return the updated permission response with HTTP 200 status
     */
    @PutMapping("/{id}")
    public PermissionResponse update(
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
     * DELETE /api/v2/permissions/{id}
     *
     * @param id the permission identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deletePermissionUseCase.delete(id);
    }
}
