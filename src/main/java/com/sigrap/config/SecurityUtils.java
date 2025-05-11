package com.sigrap.config;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations.
 * Provides helper methods for getting current user information and checking permissions.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

  private final UserRepository userRepository;

  /**
   * Gets the current authentication object from the security context.
   *
   * @return The current authentication or null if not authenticated
   */
  public Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * Gets the username of the currently authenticated user.
   *
   * @return The username or null if not authenticated
   */
  public String getCurrentUsername() {
    Authentication authentication = getCurrentAuthentication();
    return authentication != null ? authentication.getName() : null;
  }

  /**
   * Gets the currently authenticated user from the database.
   *
   * @return Optional containing the user if found, empty otherwise
   */
  public Optional<User> getCurrentUser() {
    String username = getCurrentUsername();
    return username != null
      ? userRepository.findByEmail(username)
      : Optional.empty();
  }

  /**
   * Checks if the current user is authenticated.
   *
   * @return true if authenticated, false otherwise
   */
  public boolean isAuthenticated() {
    Authentication authentication = getCurrentAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }

  /**
   * Checks if the current user has a specific role.
   *
   * @param roleName The name of the role to check
   * @return true if the user has the role, false otherwise
   */
  public boolean hasRole(String roleName) {
    return getCurrentUser()
      .map(user ->
        user
          .getRoles()
          .stream()
          .anyMatch(role -> role.getName().equals(roleName))
      )
      .orElse(false);
  }

  /**
   * Checks if the current user is the owner of an entity.
   *
   * @param entityId The ID of the entity
   * @param userId The ID of the user to check ownership against
   * @return true if the current user is the owner, false otherwise
   */
  public boolean isOwner(Long entityId, Long userId) {
    return getCurrentUser()
      .map(user -> user.getId().equals(userId))
      .orElse(false);
  }

  /**
   * Checks if the current user has permission for a specific resource and action.
   *
   * @param resource The resource to check permissions for
   * @param action The action to check permissions for
   * @return true if the user has the permission, false otherwise
   */
  public boolean hasPermission(String resource, String action) {
    return getCurrentUser()
      .map(user ->
        user
          .getRoles()
          .stream()
          .flatMap(role -> role.getPermissions().stream())
          .anyMatch(
            permission ->
              permission.getResource().equalsIgnoreCase(resource) &&
              permission.getAction().equalsIgnoreCase(action)
          )
      )
      .orElse(false);
  }
}
