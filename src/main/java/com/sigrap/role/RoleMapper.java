package com.sigrap.role;

import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionInfo;
import com.sigrap.permission.PermissionMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Role entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
@RequiredArgsConstructor
public class RoleMapper {

  private final PermissionMapper permissionMapper;

  /**
   * Converts a Role entity to a RoleInfo DTO.
   *
   * @param role The Role entity to convert
   * @return RoleInfo containing the role data
   */
  public RoleInfo toInfo(Role role) {
    if (role == null) {
      return null;
    }

    Set<PermissionInfo> permissionInfos = role.getPermissions() != null
      ? role
        .getPermissions()
        .stream()
        .map(permissionMapper::toInfo)
        .collect(Collectors.toSet())
      : Collections.emptySet();

    return RoleInfo.builder()
      .id(role.getId())
      .name(role.getName())
      .description(role.getDescription())
      .permissions(permissionInfos)
      .createdAt(role.getCreatedAt())
      .updatedAt(role.getUpdatedAt())
      .build();
  }

  /**
   * Converts a RoleData DTO to a new Role entity.
   *
   * @param roleData The RoleData DTO to convert
   * @return Role entity with data from the DTO
   */
  public Role toEntity(RoleData roleData) {
    if (roleData == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();

    return Role.builder()
      .name(roleData.getName())
      .description(roleData.getDescription())
      .permissions(new HashSet<>())
      .createdAt(now)
      .updatedAt(now)
      .build();
  }

  /**
   * Updates an existing Role entity with data from a RoleData DTO.
   *
   * @param role The Role entity to update
   * @param roleData The RoleData DTO containing the new data
   */
  public void updateEntityFromData(Role role, RoleData roleData) {
    if (role == null || roleData == null) {
      return;
    }

    if (roleData.getName() != null) {
      role.setName(roleData.getName());
    }

    if (roleData.getDescription() != null) {
      role.setDescription(roleData.getDescription());
    }

    role.setUpdatedAt(LocalDateTime.now());
  }

  /**
   * Updates the permissions of a Role entity.
   *
   * @param role The Role entity to update
   * @param permissions The set of Permission entities to assign
   */
  public void updatePermissions(Role role, Set<Permission> permissions) {
    if (role == null) {
      return;
    }

    role.setPermissions(permissions != null ? permissions : new HashSet<>());
    role.setUpdatedAt(LocalDateTime.now());
  }
}
