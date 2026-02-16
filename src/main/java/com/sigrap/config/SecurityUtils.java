package com.sigrap.config;

import com.sigrap.user.domain.model.Permission;
import com.sigrap.user.domain.model.PermissionName;
import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.RoleName;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.port.UserRepositoryPort;
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

  private final UserRepositoryPort userRepository;

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
      ? userRepository.findByEmail(new UserEmail(username))
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
   * Checks if the current user has the specified role by role name.
   *
   * @param roleName The role name to check
   * @return true if the user has the role, false otherwise
   */
  public boolean hasRole(String roleName) {
    return getCurrentUser()
      .map(user -> user.getRoles().stream()
        .anyMatch(role -> role.getName().value().equalsIgnoreCase(roleName)))
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
      .map(user -> user.getId() != null && user.getId().value().equals(userId))
      .orElse(false);
  }

  /**
   * Checks if the current user has permission for a specific resource and action.
   * This method implements a simplified role-based permission system where:
   * - Administrators have access to everything
   * - Employees have read access to most resources, and create/update access to products/categories/customers
   *
   * @param resource The resource to check permissions for
   * @param action The action to check permissions for
   * @return true if the user has the permission, false otherwise
   */
  public boolean hasPermission(String resource, String action) {
    return getCurrentUser()
      .map(user -> {
        // Check if user has ADMINISTRATOR role
        boolean isAdmin = user.getRoles().stream()
          .anyMatch(role -> role.getName().value().equalsIgnoreCase("ADMINISTRATOR"));
        
        if (isAdmin) {
          return true;
        }

        // Check if user has EMPLOYEE role
        boolean isEmployee = user.getRoles().stream()
          .anyMatch(role -> role.getName().value().equalsIgnoreCase("EMPLOYEE"));
        
        if (isEmployee) {
          if (action.equalsIgnoreCase("READ")) {
            return true;
          }

          if (
            (resource.equalsIgnoreCase("Product") ||
              resource.equalsIgnoreCase("Category")) &&
            (action.equalsIgnoreCase("CREATE") ||
              action.equalsIgnoreCase("UPDATE"))
          ) {
            return true;
          }

          if (
            resource.equalsIgnoreCase("Customer") &&
            (action.equalsIgnoreCase("CREATE") ||
              action.equalsIgnoreCase("UPDATE"))
          ) {
            return true;
          }
        }

        return false;
      })
      .orElse(false);
  }
}
