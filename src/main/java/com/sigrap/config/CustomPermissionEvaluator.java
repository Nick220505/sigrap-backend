package com.sigrap.config;

import com.sigrap.user.domain.model.Role;
import com.sigrap.user.domain.model.User;
import com.sigrap.user.domain.model.UserEmail;
import com.sigrap.user.domain.port.UserRepositoryPort;
import java.io.Serializable;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Custom permission evaluator for evaluating role-based permissions in SpEL expressions.
 * Used in @PreAuthorize annotations for access control.
 *
 * <p>This implementation provides a simplified role-based permission system where:
 * <ul>
 *   <li>Administrators have access to all resources and actions</li>
 *   <li>Employees have read access to most resources</li>
 *   <li>Employees have create/update access to specific resources (products, categories, customers)</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

  private final UserRepositoryPort userRepository;

  /**
   * Evaluates if the authenticated user has permission to perform an action on a target object.
   * In this simplified implementation, administrators have access to everything,
   * while specific permissions for employees are determined by the hasResourcePermission method.
   *
   * @param authentication The authentication object containing the user's credentials
   * @param targetDomainObject The target domain object to check permissions against (can be a String resource name)
   * @param permission The permission to check for (e.g., "READ", "WRITE")
   * @return true if the user has the permission, false otherwise
   */
  @Override
  public boolean hasPermission(
    Authentication authentication,
    Object targetDomainObject,
    Object permission
  ) {
    if (
      authentication == null || targetDomainObject == null || permission == null
    ) {
      return false;
    }

    String username = authentication.getName();
    // Handle both String resource names and actual domain objects
    String resource = targetDomainObject instanceof String 
        ? (String) targetDomainObject 
        : targetDomainObject.getClass().getSimpleName();
    String action = permission.toString();

    return hasResourcePermission(username, resource, action);
  }

  /**
   * Evaluates if the authenticated user has permission to perform an action on a target identified by ID.
   * In this simplified implementation, administrators have access to everything,
   * while specific permissions for employees are determined by the hasResourcePermission method.
   *
   * @param authentication The authentication object containing the user's credentials
   * @param targetId The ID of the target object
   * @param targetType The type of the target object
   * @param permission The permission to check for (e.g., "READ", "WRITE")
   * @return true if the user has the permission, false otherwise
   */
  @Override
  public boolean hasPermission(
    Authentication authentication,
    Serializable targetId,
    String targetType,
    Object permission
  ) {
    if (
      authentication == null ||
      targetId == null ||
      targetType == null ||
      permission == null
    ) {
      return false;
    }

    String username = authentication.getName();
    String resource = targetType;
    String action = permission.toString();

    return hasResourcePermission(username, resource, action);
  }

  /**
   * Checks if a user has a specific permission for a resource and action.
   * Administrators have access to all resources and actions.
   * Employees have limited access based on predefined rules.
   *
   * @param username The username of the user
   * @param resource The resource to check permissions for
   * @param action The action to check permissions for
   * @return true if the user has the permission, false otherwise
   */
  private boolean hasResourcePermission(
    String username,
    String resource,
    String action
  ) {
    return userRepository
      .findByEmail(new UserEmail(username))
      .map(user -> {
        // Check if user has ADMINISTRATOR role
        if (hasRoleByName(user, "ADMINISTRATOR")) {
          return true;
        }

        // Check if user has EMPLOYEE role
        if (hasRoleByName(user, "EMPLOYEE")) {
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

  /**
   * Helper method to check if a user has a role with a specific name.
   *
   * @param user The user to check
   * @param roleName The role name to check for
   * @return true if the user has the role, false otherwise
   */
  private boolean hasRoleByName(User user, String roleName) {
    return user.getRoles().stream()
      .anyMatch(role -> role.getName().value().equalsIgnoreCase(roleName));
  }

  /**
   * Checks if the authentication has any of the specified roles.
   *
   * @param authentication The authentication object
   * @param roles The roles to check for
   * @return true if the user has any of the roles, false otherwise
   */
  public boolean hasAnyRole(Authentication authentication, String... roles) {
    if (authentication == null || roles == null || roles.length == 0) {
      return false;
    }

    Collection<? extends GrantedAuthority> authorities =
      authentication.getAuthorities();
    for (String role : roles) {
      for (GrantedAuthority authority : authorities) {
        if (authority.getAuthority().equals("ROLE_" + role)) {
          return true;
        }
      }
    }

    return false;
  }
}
