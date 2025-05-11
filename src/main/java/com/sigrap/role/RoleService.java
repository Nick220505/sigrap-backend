package com.sigrap.role;

import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for role management.
 * Provides methods for creating, retrieving, updating, and deleting roles,
 * as well as managing role-permission and user-role relationships.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;
  private final RoleMapper roleMapper;

  /**
   * Retrieves all roles in the system.
   *
   * @return List of RoleInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<RoleInfo> findAll() {
    return roleRepository
      .findAll()
      .stream()
      .map(roleMapper::toInfo)
      .collect(Collectors.toList());
  }

  /**
   * Retrieves a role by its ID.
   *
   * @param id The ID of the role to retrieve
   * @return RoleInfo DTO containing the role data
   * @throws EntityNotFoundException if the role is not found
   */
  @Transactional(readOnly = true)
  public RoleInfo findById(Long id) {
    Role role = roleRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

    return roleMapper.toInfo(role);
  }

  /**
   * Creates a new role.
   *
   * @param roleData The data for the new role
   * @return RoleInfo DTO containing the created role's data
   * @throws IllegalArgumentException if the role name already exists
   */
  @Transactional
  public RoleInfo create(RoleData roleData) {
    if (roleRepository.existsByName(roleData.getName())) {
      throw new IllegalArgumentException(
        "Role name already exists: " + roleData.getName()
      );
    }

    Role role = roleMapper.toEntity(roleData);

    if (
      roleData.getPermissionIds() != null &&
      !roleData.getPermissionIds().isEmpty()
    ) {
      Set<Permission> permissions = roleData
        .getPermissionIds()
        .stream()
        .map(permissionId ->
          permissionRepository
            .findById(permissionId)
            .orElseThrow(() ->
              new EntityNotFoundException(
                "Permission not found: " + permissionId
              )
            )
        )
        .collect(Collectors.toSet());

      role.setPermissions(permissions);
    }

    Role savedRole = roleRepository.save(role);
    return roleMapper.toInfo(savedRole);
  }

  /**
   * Updates an existing role.
   *
   * @param id The ID of the role to update
   * @param roleData The new data for the role
   * @return RoleInfo DTO containing the updated role's data
   * @throws EntityNotFoundException if the role is not found
   * @throws IllegalArgumentException if the new name conflicts with an existing role
   */
  @Transactional
  public RoleInfo update(Long id, RoleData roleData) {
    Role role = roleRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

    if (
      roleData.getName() != null &&
      !roleData.getName().equals(role.getName()) &&
      roleRepository.existsByName(roleData.getName())
    ) {
      throw new IllegalArgumentException(
        "Role name already exists: " + roleData.getName()
      );
    }

    roleMapper.updateEntityFromData(role, roleData);

    if (roleData.getPermissionIds() != null) {
      Set<Permission> permissions = roleData
        .getPermissionIds()
        .stream()
        .map(permissionId ->
          permissionRepository
            .findById(permissionId)
            .orElseThrow(() ->
              new EntityNotFoundException(
                "Permission not found: " + permissionId
              )
            )
        )
        .collect(Collectors.toSet());

      roleMapper.updatePermissions(role, permissions);
    }

    Role updatedRole = roleRepository.save(role);
    return roleMapper.toInfo(updatedRole);
  }

  /**
   * Deletes a role by its ID.
   *
   * @param id The ID of the role to delete
   * @throws EntityNotFoundException if the role is not found
   * @throws IllegalArgumentException if the role is still assigned to users
   */
  @Transactional
  public void delete(Long id) {
    Role role = roleRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

    if (!role.getUsers().isEmpty()) {
      throw new IllegalArgumentException(
        "Cannot delete role that is still assigned to users"
      );
    }

    roleRepository.delete(role);
  }

  /**
   * Assigns a role to a user.
   *
   * @param roleId The ID of the role to assign
   * @param userId The ID of the user to assign the role to
   * @return RoleInfo DTO containing the role's data
   * @throws EntityNotFoundException if the role or user is not found
   */
  @Transactional
  public RoleInfo assignToUser(Long roleId, Long userId) {
    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    Set<Role> userRoles = user.getRoles();
    if (userRoles == null) {
      userRoles = new HashSet<>();
      user.setRoles(userRoles);
    }

    userRoles.add(role);
    userRepository.save(user);

    return roleMapper.toInfo(role);
  }

  /**
   * Removes a role from a user.
   *
   * @param roleId The ID of the role to remove
   * @param userId The ID of the user to remove the role from
   * @throws EntityNotFoundException if the role or user is not found
   */
  @Transactional
  public void removeFromUser(Long roleId, Long userId) {
    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    Set<Role> userRoles = user.getRoles();
    if (userRoles != null) {
      userRoles.remove(role);
      userRepository.save(user);
    }
  }
}
