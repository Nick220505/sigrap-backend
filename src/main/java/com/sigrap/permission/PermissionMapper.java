package com.sigrap.permission;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Permission entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
public class PermissionMapper {

  /**
   * Converts a Permission entity to a PermissionInfo DTO.
   *
   * @param permission The Permission entity to convert
   * @return PermissionInfo containing the permission data
   */
  public PermissionInfo toInfo(Permission permission) {
    if (permission == null) {
      return null;
    }

    return PermissionInfo.builder()
      .id(permission.getId())
      .name(permission.getName())
      .description(permission.getDescription())
      .resource(permission.getResource())
      .action(permission.getAction())
      .createdAt(permission.getCreatedAt())
      .updatedAt(permission.getUpdatedAt())
      .build();
  }

  /**
   * Converts a PermissionData DTO to a new Permission entity.
   *
   * @param permissionData The PermissionData DTO to convert
   * @return Permission entity with data from the DTO
   */
  public Permission toEntity(PermissionData permissionData) {
    if (permissionData == null) {
      return null;
    }

    LocalDateTime now = LocalDateTime.now();

    return Permission.builder()
      .name(permissionData.getName())
      .description(permissionData.getDescription())
      .resource(permissionData.getResource())
      .action(permissionData.getAction())
      .createdAt(now)
      .updatedAt(now)
      .build();
  }

  /**
   * Updates an existing Permission entity with data from a PermissionData DTO.
   *
   * @param permission The Permission entity to update
   * @param permissionData The PermissionData DTO containing the new data
   */
  public void updateEntityFromData(
    Permission permission,
    PermissionData permissionData
  ) {
    if (permission == null || permissionData == null) {
      return;
    }

    if (permissionData.getName() != null) {
      permission.setName(permissionData.getName());
    }

    if (permissionData.getDescription() != null) {
      permission.setDescription(permissionData.getDescription());
    }

    if (permissionData.getResource() != null) {
      permission.setResource(permissionData.getResource());
    }

    if (permissionData.getAction() != null) {
      permission.setAction(permissionData.getAction());
    }

    permission.setUpdatedAt(LocalDateTime.now());
  }
}
