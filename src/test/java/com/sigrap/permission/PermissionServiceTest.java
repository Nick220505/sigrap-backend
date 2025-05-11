package com.sigrap.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

  @Mock
  private PermissionRepository permissionRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PermissionMapper permissionMapper;

  @InjectMocks
  private PermissionService permissionService;

  @Test
  void findAll_shouldReturnAllPermissions() {
    Permission permission1 = Permission.builder()
      .id(1L)
      .name("PERMISSION_1")
      .resource("RESOURCE_1")
      .action("ACTION_1")
      .build();

    Permission permission2 = Permission.builder()
      .id(2L)
      .name("PERMISSION_2")
      .resource("RESOURCE_2")
      .action("ACTION_2")
      .build();

    List<Permission> permissions = List.of(permission1, permission2);

    PermissionInfo permissionInfo1 = PermissionInfo.builder()
      .id(1L)
      .name("PERMISSION_1")
      .resource("RESOURCE_1")
      .action("ACTION_1")
      .build();

    PermissionInfo permissionInfo2 = PermissionInfo.builder()
      .id(2L)
      .name("PERMISSION_2")
      .resource("RESOURCE_2")
      .action("ACTION_2")
      .build();

    when(permissionRepository.findAll()).thenReturn(permissions);
    when(permissionMapper.toInfo(permission1)).thenReturn(permissionInfo1);
    when(permissionMapper.toInfo(permission2)).thenReturn(permissionInfo2);

    List<PermissionInfo> result = permissionService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("PERMISSION_1");
    assertThat(result.get(1).getId()).isEqualTo(2L);
    assertThat(result.get(1).getName()).isEqualTo("PERMISSION_2");

    verify(permissionRepository).findAll();
    verify(permissionMapper).toInfo(permission1);
    verify(permissionMapper).toInfo(permission2);
  }

  @Test
  void findById_shouldReturnPermission_whenExists() {
    Long id = 1L;
    Permission permission = Permission.builder()
      .id(id)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .build();

    PermissionInfo permissionInfo = PermissionInfo.builder()
      .id(id)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .build();

    when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
    when(permissionMapper.toInfo(permission)).thenReturn(permissionInfo);

    PermissionInfo result = permissionService.findById(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getName()).isEqualTo("TEST_PERMISSION");

    verify(permissionRepository).findById(id);
    verify(permissionMapper).toInfo(permission);
  }

  @Test
  void findById_shouldThrowException_whenDoesNotExist() {
    Long id = 1L;

    when(permissionRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      permissionService.findById(id);
    });

    verify(permissionRepository).findById(id);
  }

  @Test
  void create_shouldCreatePermission() {
    PermissionData permissionData = PermissionData.builder()
      .name("NEW_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .build();

    Permission permission = Permission.builder()
      .name("NEW_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    Permission savedPermission = Permission.builder()
      .id(1L)
      .name("NEW_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .createdAt(permission.getCreatedAt())
      .updatedAt(permission.getUpdatedAt())
      .build();

    PermissionInfo permissionInfo = PermissionInfo.builder()
      .id(1L)
      .name("NEW_PERMISSION")
      .resource("NEW_RESOURCE")
      .action("NEW_ACTION")
      .createdAt(permission.getCreatedAt())
      .updatedAt(permission.getUpdatedAt())
      .build();

    when(
      permissionRepository.existsByName(permissionData.getName())
    ).thenReturn(false);
    when(
      permissionRepository.findByResourceAndAction(
        permissionData.getResource(),
        permissionData.getAction()
      )
    ).thenReturn(Optional.empty());
    when(permissionMapper.toEntity(permissionData)).thenReturn(permission);
    when(permissionRepository.save(permission)).thenReturn(savedPermission);
    when(permissionMapper.toInfo(savedPermission)).thenReturn(permissionInfo);

    PermissionInfo result = permissionService.create(permissionData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("NEW_PERMISSION");

    verify(permissionRepository).existsByName(permissionData.getName());
    verify(permissionRepository).findByResourceAndAction(
      permissionData.getResource(),
      permissionData.getAction()
    );
    verify(permissionMapper).toEntity(permissionData);
    verify(permissionRepository).save(permission);
    verify(permissionMapper).toInfo(savedPermission);
  }

  @Test
  void create_shouldThrowException_whenNameExists() {
    PermissionData permissionData = PermissionData.builder()
      .name("EXISTING_PERMISSION")
      .resource("RESOURCE")
      .action("ACTION")
      .build();

    when(
      permissionRepository.existsByName(permissionData.getName())
    ).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      permissionService.create(permissionData);
    });

    verify(permissionRepository).existsByName(permissionData.getName());
    verify(permissionRepository, never()).save(any());
  }

  @Test
  void assignToRole_shouldAssignPermissionToRole() {
    Long permissionId = 1L;
    Long roleId = 1L;

    Permission permission = Permission.builder()
      .id(permissionId)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .build();

    Role role = Role.builder()
      .id(roleId)
      .name("TEST_ROLE")
      .permissions(new java.util.HashSet<>())
      .build();

    PermissionInfo permissionInfo = PermissionInfo.builder()
      .id(permissionId)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .build();

    when(permissionRepository.findById(permissionId)).thenReturn(
      Optional.of(permission)
    );
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(permissionMapper.toInfo(permission)).thenReturn(permissionInfo);

    PermissionInfo result = permissionService.assignToRole(
      permissionId,
      roleId
    );

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(permissionId);
    assertThat(role.getPermissions()).contains(permission);

    verify(permissionRepository).findById(permissionId);
    verify(roleRepository).findById(roleId);
    verify(roleRepository).save(role);
    verify(permissionMapper).toInfo(permission);
  }

  @Test
  void removeFromRole_shouldRemovePermissionFromRole() {
    Long permissionId = 1L;
    Long roleId = 1L;

    Permission permission = Permission.builder()
      .id(permissionId)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .build();

    Set<Permission> permissions = new java.util.HashSet<>();
    permissions.add(permission);

    Role role = Role.builder()
      .id(roleId)
      .name("TEST_ROLE")
      .permissions(permissions)
      .build();

    when(permissionRepository.findById(permissionId)).thenReturn(
      Optional.of(permission)
    );
    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

    permissionService.removeFromRole(permissionId, roleId);

    assertThat(role.getPermissions()).doesNotContain(permission);

    verify(permissionRepository).findById(permissionId);
    verify(roleRepository).findById(roleId);
    verify(roleRepository).save(role);
  }

  @Test
  void delete_shouldDeletePermission_whenNoRoles() {
    Long id = 1L;
    Permission permission = Permission.builder()
      .id(id)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .roles(Set.of())
      .build();

    when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));

    permissionService.delete(id);

    verify(permissionRepository).findById(id);
    verify(permissionRepository).delete(permission);
  }

  @Test
  void delete_shouldThrowException_whenAssignedToRoles() {
    Long id = 1L;
    Role role = Role.builder().id(1L).name("ROLE").build();

    Permission permission = Permission.builder()
      .id(id)
      .name("TEST_PERMISSION")
      .resource("TEST_RESOURCE")
      .action("TEST_ACTION")
      .roles(Set.of(role))
      .build();

    when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));

    assertThrows(IllegalArgumentException.class, () -> {
      permissionService.delete(id);
    });

    verify(permissionRepository).findById(id);
    verify(permissionRepository, never()).delete(any());
  }
}
