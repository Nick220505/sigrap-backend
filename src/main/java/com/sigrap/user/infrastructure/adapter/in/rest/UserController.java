package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.application.port.in.command.UpdateUserCommand;
import com.sigrap.user.domain.model.User;
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
 * REST controller for user operations.
 * This is an input adapter that translates HTTP requests to use case calls.
 * It handles HTTP concerns (validation, status codes, request/response mapping)
 * and delegates business logic to use cases.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users, roles, and permissions")
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final RemoveRoleUseCase removeRoleUseCase;
    private final UserResponseMapper responseMapper;
    
    /**
     * Constructor injection of use cases and mapper.
     */
    public UserController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            EnableUserUseCase enableUserUseCase,
            DisableUserUseCase disableUserUseCase,
            AssignRoleUseCase assignRoleUseCase,
            RemoveRoleUseCase removeRoleUseCase,
            UserResponseMapper responseMapper) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.enableUserUseCase = enableUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.removeRoleUseCase = removeRoleUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new user.
     * POST /api/users
     *
     * @param request the user creation request
     * @return the created user response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user account with the provided username, email, and password. " +
                      "The username and email must be unique across the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "User created successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors or duplicate username/email"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        CreateUserCommand command = new CreateUserCommand(
            request.username(),
            request.email(),
            request.password()
        );
        User user = createUserUseCase.create(command);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves a user by its ID.
     * GET /api/users/{id}
     *
     * @param id the user identifier
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user account by its unique identifier."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse getById(
        @Parameter(description = "User unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        User user = getUserUseCase.getById(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves a user by username.
     * GET /api/users/username/{username}
     *
     * @param username the username
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/username/{username}")
    @Operation(
        summary = "Get user by username",
        description = "Retrieves a user account by its unique username."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse getByUsername(
        @Parameter(description = "Username", required = true, example = "johndoe")
        @PathVariable String username
    ) {
        User user = getUserUseCase.getByUsername(username);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves a user by email.
     * GET /api/users/email/{email}
     *
     * @param email the email address
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get user by email",
        description = "Retrieves a user account by its unique email address."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse getByEmail(
        @Parameter(description = "Email address", required = true, example = "john.doe@example.com")
        @PathVariable String email
    ) {
        User user = getUserUseCase.getByEmail(email);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves all users.
     * GET /api/users
     *
     * @return a list of all user responses with HTTP 200 status
     */
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all user accounts in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of users retrieved successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<UserResponse> getAll() {
        return getUserUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all enabled users.
     * GET /api/users/enabled
     *
     * @return a list of enabled user responses with HTTP 200 status
     */
    @GetMapping("/enabled")
    @Operation(
        summary = "Get all enabled users",
        description = "Retrieves a list of all enabled user accounts in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of enabled users retrieved successfully"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public List<UserResponse> getAllEnabled() {
        return getUserUseCase.getAllEnabled().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing user.
     * PUT /api/users/{id}
     *
     * @param id the user identifier
     * @param request the user update request
     * @return the updated user response with HTTP 200 status
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing user",
        description = "Updates an existing user account with the provided email and/or password."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User updated successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - validation errors"
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse update(
            @Parameter(description = "User unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand(
            request.email(),
            request.password()
        );
        User user = updateUserUseCase.update(id, command);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Enables a user account.
     * POST /api/users/{id}/enable
     *
     * @param id the user identifier
     * @return the updated user response with HTTP 200 status
     */
    @PostMapping("/{id}/enable")
    @Operation(
        summary = "Enable a user account",
        description = "Enables a disabled user account, allowing the user to log in and access the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User enabled successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse enable(
        @Parameter(description = "User unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        User user = enableUserUseCase.enable(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Disables a user account.
     * POST /api/users/{id}/disable
     *
     * @param id the user identifier
     * @return the updated user response with HTTP 200 status
     */
    @PostMapping("/{id}/disable")
    @Operation(
        summary = "Disable a user account",
        description = "Disables an active user account, preventing the user from logging in and accessing the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "User disabled successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse disable(
        @Parameter(description = "User unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        User user = disableUserUseCase.disable(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Assigns a role to a user.
     * POST /api/users/{userId}/roles/{roleId}
     *
     * @param userId the user identifier
     * @param roleId the role identifier
     * @return the updated user response with HTTP 200 status
     */
    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(
        summary = "Assign a role to a user",
        description = "Assigns a specific role to a user, granting the user all permissions associated with that role."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Role assigned successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User or role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse assignRole(
            @Parameter(description = "User unique identifier", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Role unique identifier", required = true, example = "2")
            @PathVariable Long roleId) {
        User user = assignRoleUseCase.assignRole(userId, roleId);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Removes a role from a user.
     * DELETE /api/users/{userId}/roles/{roleId}
     *
     * @param userId the user identifier
     * @param roleId the role identifier
     * @return the updated user response with HTTP 200 status
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(
        summary = "Remove a role from a user",
        description = "Removes a specific role from a user, revoking all permissions associated with that role."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Role removed successfully",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "User or role not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public UserResponse removeRole(
            @Parameter(description = "User unique identifier", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Role unique identifier", required = true, example = "2")
            @PathVariable Long roleId) {
        User user = removeRoleUseCase.removeRole(userId, roleId);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Deletes a user by its ID.
     * DELETE /api/users/{id}
     *
     * @param id the user identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a user",
        description = "Permanently deletes a user account from the system."
    )
    @ApiResponse(
        responseCode = "204",
        description = "User deleted successfully"
    )
    @ApiResponse(
        responseCode = "404",
        description = "User not found"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    public void delete(
        @Parameter(description = "User unique identifier", required = true, example = "1")
        @PathVariable Long id
    ) {
        deleteUserUseCase.delete(id);
    }
}
