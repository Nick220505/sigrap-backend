package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreateRoleCommand;
import com.sigrap.user.application.port.in.command.UpdateRoleCommand;
import com.sigrap.user.domain.model.Role;
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
@RequestMapping("/api/v2/roles")
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
     * POST /api/v2/roles
     *
     * @param request the role creation request
     * @return the created role response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
     * GET /api/v2/roles/{id}
     *
     * @param id the role identifier
     * @return the role response with HTTP 200 status
     */
    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable Long id) {
        Role role = getRoleUseCase.getById(id);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Retrieves a role by name.
     * GET /api/v2/roles/name/{name}
     *
     * @param name the role name
     * @return the role response with HTTP 200 status
     */
    @GetMapping("/name/{name}")
    public RoleResponse getByName(@PathVariable String name) {
        Role role = getRoleUseCase.getByName(name);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Retrieves all roles.
     * GET /api/v2/roles
     *
     * @return a list of all role responses with HTTP 200 status
     */
    @GetMapping
    public List<RoleResponse> getAll() {
        return getRoleUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing role.
     * PUT /api/v2/roles/{id}
     *
     * @param id the role identifier
     * @param request the role update request
     * @return the updated role response with HTTP 200 status
     */
    @PutMapping("/{id}")
    public RoleResponse update(
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
     * POST /api/v2/roles/{roleId}/permissions/{permissionId}
     *
     * @param roleId the role identifier
     * @param permissionId the permission identifier
     * @return the updated role response with HTTP 200 status
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponse assignPermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        Role role = assignPermissionUseCase.assignPermission(roleId, permissionId);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Removes a permission from a role.
     * DELETE /api/v2/roles/{roleId}/permissions/{permissionId}
     *
     * @param roleId the role identifier
     * @param permissionId the permission identifier
     * @return the updated role response with HTTP 200 status
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponse removePermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        Role role = removePermissionUseCase.removePermission(roleId, permissionId);
        return responseMapper.toResponse(role);
    }
    
    /**
     * Deletes a role by its ID.
     * DELETE /api/v2/roles/{id}
     *
     * @param id the role identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteRoleUseCase.delete(id);
    }
}
