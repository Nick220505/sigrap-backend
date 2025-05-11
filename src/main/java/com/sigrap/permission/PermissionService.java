package com.sigrap.permission;

import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for permission management.
 * Provides methods for creating, retrieving, updating, and deleting permissions,
 * as well as managing permission-role relationships.
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final PermissionMapper permissionMapper;

  /**
   * Retrieves all permissions in the system.
   *
   * @return List of PermissionInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<PermissionInfo> findAll() {
    return permissionRepository
      .findAll()
      .stream()
      .map(permissionMapper::toInfo)
      .collect(Collectors.toList());
  }

  /**
   * Retrieves a permission by its ID.
   *
   * @param id The ID of the permission to retrieve
   * @return PermissionInfo DTO containing the permission data
   * @throws EntityNotFoundException if the permission is not found
   */
  @Transactional(readOnly = true)
  public PermissionInfo findById(Long id) {
    Permission permission = permissionRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Permission not found: " + id)
      );

    return permissionMapper.toInfo(permission);
  }

  /**
   * Retrieves permissions by resource.
   *
   * @param resource The resource to filter permissions by
   * @return List of PermissionInfo DTOs for the specified resource
   */
  @Transactional(readOnly = true)
  public List<PermissionInfo> findByResource(String resource) {
    return permissionRepository
      .findByResource(resource)
      .stream()
      .map(permissionMapper::toInfo)
      .collect(Collectors.toList());
  }

  /**
   * Creates a new permission.
   *
   * @param permissionData The data for the new permission
   * @return PermissionInfo DTO containing the created permission's data
   * @throws IllegalArgumentException if the permission name already exists
   */
  @Transactional
  public PermissionInfo create(PermissionData permissionData) {
    if (permissionRepository.existsByName(permissionData.getName())) {
      throw new IllegalArgumentException(
        "Permission name already exists: " + permissionData.getName()
      );
    }

    if (
      permissionRepository
        .findByResourceAndAction(
          permissionData.getResource(),
          permissionData.getAction()
        )
        .isPresent()
    ) {
      throw new IllegalArgumentException(
        "Permission for resource '" +
        permissionData.getResource() +
        "' and action '" +
        permissionData.getAction() +
        "' already exists"
      );
    }

    Permission permission = permissionMapper.toEntity(permissionData);
    Permission savedPermission = permissionRepository.save(permission);

    return permissionMapper.toInfo(savedPermission);
  }

  /**
   * Updates an existing permission.
   *
   * @param id The ID of the permission to update
   * @param permissionData The new data for the permission
   * @return PermissionInfo DTO containing the updated permission's data
   * @throws EntityNotFoundException if the permission is not found
   * @throws IllegalArgumentException if the new name conflicts with an existing permission
   */
  @Transactional
  public PermissionInfo update(Long id, PermissionData permissionData) {
    Permission permission = permissionRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Permission not found: " + id)
      );

    if (
      permissionData.getName() != null &&
      !permissionData.getName().equals(permission.getName()) &&
      permissionRepository.existsByName(permissionData.getName())
    ) {
      throw new IllegalArgumentException(
        "Permission name already exists: " + permissionData.getName()
      );
    }

    if (
      permissionData.getResource() != null &&
      permissionData.getAction() != null &&
      (!permissionData.getResource().equals(permission.getResource()) ||
        !permissionData.getAction().equals(permission.getAction()))
    ) {
      if (
        permissionRepository
          .findByResourceAndAction(
            permissionData.getResource(),
            permissionData.getAction()
          )
          .isPresent()
      ) {
        throw new IllegalArgumentException(
          "Permission for resource '" +
          permissionData.getResource() +
          "' and action '" +
          permissionData.getAction() +
          "' already exists"
        );
      }
    }

    permissionMapper.updateEntityFromData(permission, permissionData);
    Permission updatedPermission = permissionRepository.save(permission);

    return permissionMapper.toInfo(updatedPermission);
  }

  /**
   * Deletes a permission by its ID.
   *
   * @param id The ID of the permission to delete
   * @throws EntityNotFoundException if the permission is not found
   * @throws IllegalArgumentException if the permission is still assigned to roles
   */
  @Transactional
  public void delete(Long id) {
    Permission permission = permissionRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Permission not found: " + id)
      );

    if (!permission.getRoles().isEmpty()) {
      throw new IllegalArgumentException(
        "Cannot delete permission that is still assigned to roles"
      );
    }

    permissionRepository.delete(permission);
  }

  /**
   * Assigns a permission to a role.
   *
   * @param permissionId The ID of the permission to assign
   * @param roleId The ID of the role to assign the permission to
   * @return PermissionInfo DTO containing the permission's data
   * @throws EntityNotFoundException if the permission or role is not found
   */
  @Transactional
  public PermissionInfo assignToRole(Long permissionId, Long roleId) {
    Permission permission = permissionRepository
      .findById(permissionId)
      .orElseThrow(() ->
        new EntityNotFoundException("Permission not found: " + permissionId)
      );

    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    Set<Permission> rolePermissions = role.getPermissions();
    rolePermissions.add(permission);
    roleRepository.save(role);

    return permissionMapper.toInfo(permission);
  }

  /**
   * Removes a permission from a role.
   *
   * @param permissionId The ID of the permission to remove
   * @param roleId The ID of the role to remove the permission from
   * @throws EntityNotFoundException if the permission or role is not found
   */
  @Transactional
  public void removeFromRole(Long permissionId, Long roleId) {
    Permission permission = permissionRepository
      .findById(permissionId)
      .orElseThrow(() ->
        new EntityNotFoundException("Permission not found: " + permissionId)
      );

    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    Set<Permission> rolePermissions = role.getPermissions();
    rolePermissions.remove(permission);
    roleRepository.save(role);
  }
}
