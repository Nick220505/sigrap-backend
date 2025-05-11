package com.sigrap.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * REST controller for user management operations.
 * Provides endpoints for user CRUD operations, profile management, and account actions.
 */
@RestController
@RequestMapping("/api/users")
@Tag(
  name = "User Management",
  description = "API for user management operations"
)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Gets all users.
   *
   * @return List of all users
   */
  @GetMapping
  @Operation(
    summary = "Get all users",
    description = "Retrieves all users in the system"
  )
  public List<UserInfo> getAll() {
    return userService.findAll();
  }

  /**
   * Gets a user by ID.
   *
   * @param id The ID of the user to retrieve
   * @return The requested user information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get user by ID",
    description = "Retrieves a user by their ID"
  )
  public UserInfo getById(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    return userService.findById(id);
  }

  /**
   * Gets the current user's profile.
   *
   * @return The current user's information
   */
  @GetMapping("/me")
  @Operation(
    summary = "Get current user",
    description = "Retrieves the currently authenticated user's profile"
  )
  public UserInfo getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext()
      .getAuthentication();
    return userService.findByEmail(auth.getName());
  }

  /**
   * Creates a new user.
   *
   * @param userData Data for the new user
   * @return The created user information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create user", description = "Creates a new user")
  public UserInfo create(@Valid @RequestBody UserData userData) {
    return userService.create(userData);
  }

  /**
   * Updates a user.
   *
   * @param id The ID of the user to update
   * @param userData New data for the user
   * @return The updated user information
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update user", description = "Updates an existing user")
  public UserInfo update(
    @Parameter(description = "User ID") @PathVariable Long id,
    @Valid @RequestBody UserData userData
  ) {
    return userService.update(id, userData);
  }

  /**
   * Updates the current user's profile.
   *
   * @param userData New profile data
   * @return The updated user information
   */
  @PutMapping("/me")
  @Operation(
    summary = "Update current user",
    description = "Updates the currently authenticated user's profile"
  )
  public UserInfo updateCurrentUser(@RequestBody UserData userData) {
    Authentication auth = SecurityContextHolder.getContext()
      .getAuthentication();
    UserInfo currentUser = userService.findByEmail(auth.getName());
    return userService.updateProfile(currentUser.getId(), userData);
  }

  /**
   * Deletes a user.
   *
   * @param id The ID of the user to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete user", description = "Deletes a user")
  public void delete(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    userService.delete(id);
  }

  /**
   * Changes a user's password.
   *
   * @param passwordChangeRequest Request containing current and new passwords
   * @return The updated user information
   */
  @PostMapping("/me/change-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Change password",
    description = "Changes a user's password"
  )
  public UserInfo changePassword(
    @Valid @RequestBody PasswordChangeRequest passwordChangeRequest
  ) {
    Authentication auth = SecurityContextHolder.getContext()
      .getAuthentication();
    UserInfo currentUser = userService.findByEmail(auth.getName());
    return userService.changePassword(
      currentUser.getId(),
      passwordChangeRequest.getCurrentPassword(),
      passwordChangeRequest.getNewPassword()
    );
  }

  /**
   * Initiates a password reset.
   *
   * @param resetRequest Request containing the user's email
   * @return Message indicating reset token has been sent
   */
  @PostMapping("/reset-password")
  @Operation(
    summary = "Request password reset",
    description = "Initiates a password reset process"
  )
  public UserInfo resetPasswordRequest(
    @Valid @RequestBody PasswordResetRequest resetRequest
  ) {
    return userService.resetPassword(
      resetRequest.getEmail(),
      "newResetPassword"
    );
  }

  /**
   * Completes a password reset.
   *
   * @param resetConfirmRequest Request containing the reset token and new password
   * @return Message indicating password has been reset
   */
  @PostMapping("/reset-password/confirm")
  @Operation(
    summary = "Complete password reset",
    description = "Completes a password reset process"
  )
  public UserInfo resetPasswordConfirm(
    @Valid @RequestBody PasswordResetConfirmRequest resetConfirmRequest
  ) {
    return userService.resetPassword(
      resetConfirmRequest.getToken(),
      resetConfirmRequest.getNewPassword()
    );
  }

  /**
   * Locks a user's account.
   *
   * @param id The ID of the user to lock
   * @return The updated user information
   */
  @PutMapping("/{id}/lock")
  @Operation(
    summary = "Lock user account",
    description = "Locks a user's account"
  )
  public UserInfo lockAccount(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    return userService.lockAccount(id);
  }

  /**
   * Unlocks a user's account.
   *
   * @param id The ID of the user to unlock
   * @return The updated user information
   */
  @PutMapping("/{id}/unlock")
  @Operation(
    summary = "Unlock user account",
    description = "Unlocks a user's account"
  )
  public UserInfo unlockAccount(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    return userService.unlockAccount(id);
  }

  /**
   * Locks a user's account (POST variant).
   *
   * @param id The ID of the user to lock
   * @return The updated user information
   */
  @PostMapping("/{id}/lock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Lock user account (POST)",
    description = "Locks a user's account using POST"
  )
  public UserInfo lockAccountPost(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    return userService.lockAccount(id);
  }

  /**
   * Unlocks a user's account (POST variant).
   *
   * @param id The ID of the user to unlock
   * @return The updated user information
   */
  @PostMapping("/{id}/unlock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Unlock user account (POST)",
    description = "Unlocks a user's account using POST"
  )
  public UserInfo unlockAccountPost(
    @Parameter(description = "User ID") @PathVariable Long id
  ) {
    return userService.unlockAccount(id);
  }

  /**
   * Assigns a role to a user.
   *
   * @param userId The ID of the user
   * @param roleId The ID of the role to assign
   * @return The updated user information
   */
  @PutMapping("/{userId}/roles/{roleId}")
  @Operation(
    summary = "Assign role to user",
    description = "Assigns a role to a user"
  )
  public UserInfo assignRole(
    @Parameter(description = "User ID") @PathVariable Long userId,
    @Parameter(description = "Role ID") @PathVariable Long roleId
  ) {
    return userService.assignRole(userId, roleId);
  }

  /**
   * Removes a role from a user.
   *
   * @param userId The ID of the user
   * @param roleId The ID of the role to remove
   * @return The updated user information
   */
  @DeleteMapping("/{userId}/roles/{roleId}")
  @Operation(
    summary = "Remove role from user",
    description = "Removes a role from a user"
  )
  public UserInfo removeRole(
    @Parameter(description = "User ID") @PathVariable Long userId,
    @Parameter(description = "Role ID") @PathVariable Long roleId
  ) {
    return userService.removeRole(userId, roleId);
  }

  /**
   * Resets a user's password by ID.
   *
   * @param id The ID of the user to reset the password for
   * @param passwordResetData Request containing the new password
   * @return The updated user information
   */
  @PostMapping("/{id}/reset-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Reset user password",
    description = "Resets a user's password by admin"
  )
  public UserInfo resetUserPassword(
    @Parameter(description = "User ID") @PathVariable Long id,
    @Valid @RequestBody Map<String, String> passwordResetData
  ) {
    return userService.changePassword(
      id,
      null,
      passwordResetData.get("newPassword")
    );
  }
}
