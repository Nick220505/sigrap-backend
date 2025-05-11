package com.sigrap.config;

import com.sigrap.permission.Permission;
import com.sigrap.role.Role;
import com.sigrap.user.UserRepository;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Custom permission evaluator for evaluating permissions in SpEL expressions.
 * Used in @PreAuthorize annotations for fine-grained access control.
 */
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

  private final UserRepository userRepository;

  /**
   * Evaluates if the authenticated user has permission to perform an action on a target object.
   *
   * @param authentication The authentication object containing the user's credentials
   * @param targetDomainObject The target domain object to check permissions against
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
    String resource = targetDomainObject.getClass().getSimpleName();
    String action = permission.toString();

    return hasResourcePermission(username, resource, action);
  }

  /**
   * Evaluates if the authenticated user has permission to perform an action on a target identified by ID.
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
      .findByEmail(username)
      .map(user -> {
        Set<Permission> permissions = user
          .getRoles()
          .stream()
          .map(Role::getPermissions)
          .flatMap(Set::stream)
          .collect(Collectors.toSet());

        return permissions
          .stream()
          .anyMatch(
            p ->
              p.getResource().equalsIgnoreCase(resource) &&
              p.getAction().equalsIgnoreCase(action)
          );
      })
      .orElse(false);
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
