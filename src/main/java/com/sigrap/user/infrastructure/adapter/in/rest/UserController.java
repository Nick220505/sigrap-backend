package com.sigrap.user.infrastructure.adapter.in.rest;

import com.sigrap.user.application.port.in.*;
import com.sigrap.user.application.port.in.command.CreateUserCommand;
import com.sigrap.user.application.port.in.command.UpdateUserCommand;
import com.sigrap.user.domain.model.User;
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
@RequestMapping("/api/v2/users")
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
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
            UserResponseMapper responseMapper) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.enableUserUseCase = enableUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Creates a new user.
     * POST /api/v2/users
     *
     * @param request the user creation request
     * @return the created user response with HTTP 201 status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
     * GET /api/v2/users/{id}
     *
     * @param id the user identifier
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        User user = getUserUseCase.getById(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves a user by username.
     * GET /api/v2/users/username/{username}
     *
     * @param username the username
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/username/{username}")
    public UserResponse getByUsername(@PathVariable String username) {
        User user = getUserUseCase.getByUsername(username);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves a user by email.
     * GET /api/v2/users/email/{email}
     *
     * @param email the email address
     * @return the user response with HTTP 200 status
     */
    @GetMapping("/email/{email}")
    public UserResponse getByEmail(@PathVariable String email) {
        User user = getUserUseCase.getByEmail(email);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Retrieves all users.
     * GET /api/v2/users
     *
     * @return a list of all user responses with HTTP 200 status
     */
    @GetMapping
    public List<UserResponse> getAll() {
        return getUserUseCase.getAll().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Retrieves all enabled users.
     * GET /api/v2/users/enabled
     *
     * @return a list of enabled user responses with HTTP 200 status
     */
    @GetMapping("/enabled")
    public List<UserResponse> getAllEnabled() {
        return getUserUseCase.getAllEnabled().stream()
            .map(responseMapper::toResponse)
            .toList();
    }
    
    /**
     * Updates an existing user.
     * PUT /api/v2/users/{id}
     *
     * @param id the user identifier
     * @param request the user update request
     * @return the updated user response with HTTP 200 status
     */
    @PutMapping("/{id}")
    public UserResponse update(
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
     * POST /api/v2/users/{id}/enable
     *
     * @param id the user identifier
     * @return the updated user response with HTTP 200 status
     */
    @PostMapping("/{id}/enable")
    public UserResponse enable(@PathVariable Long id) {
        User user = enableUserUseCase.enable(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Disables a user account.
     * POST /api/v2/users/{id}/disable
     *
     * @param id the user identifier
     * @return the updated user response with HTTP 200 status
     */
    @PostMapping("/{id}/disable")
    public UserResponse disable(@PathVariable Long id) {
        User user = disableUserUseCase.disable(id);
        return responseMapper.toResponse(user);
    }
    
    /**
     * Deletes a user by its ID.
     * DELETE /api/v2/users/{id}
     *
     * @param id the user identifier
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteUserUseCase.delete(id);
    }
}
