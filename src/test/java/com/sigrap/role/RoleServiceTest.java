package com.sigrap.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionInfo;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PermissionRepository permissionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleMapper roleMapper;

  @InjectMocks
  private RoleService roleService;

  @Test
  void findAll_shouldReturnAllRoles() {
    Role role1 = Role.builder().id(1L).name("ROLE_1").build();
    Role role2 = Role.builder().id(2L).name("ROLE_2").build();

    List<Role> roles = List.of(role1, role2);

    RoleInfo roleInfo1 = RoleInfo.builder().id(1L).name("ROLE_1").build();
    RoleInfo roleInfo2 = RoleInfo.builder().id(2L).name("ROLE_2").build();

    when(roleRepository.findAll()).thenReturn(roles);
    when(roleMapper.toInfo(role1)).thenReturn(roleInfo1);
    when(roleMapper.toInfo(role2)).thenReturn(roleInfo2);

    List<RoleInfo> result = roleService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("ROLE_1");
    assertThat(result.get(1).getId()).isEqualTo(2L);
    assertThat(result.get(1).getName()).isEqualTo("ROLE_2");

    verify(roleRepository).findAll();
    verify(roleMapper).toInfo(role1);
    verify(roleMapper).toInfo(role2);
  }

  @Test
  void findById_shouldReturnRole_whenExists() {
    Long id = 1L;
    Role role = Role.builder()
      .id(id)
      .name("TEST_ROLE")
      .description("Test Role Description")
      .build();

    RoleInfo roleInfo = RoleInfo.builder()
      .id(id)
      .name("TEST_ROLE")
      .description("Test Role Description")
      .build();

    when(roleRepository.findById(id)).thenReturn(Optional.of(role));
    when(roleMapper.toInfo(role)).thenReturn(roleInfo);

    RoleInfo result = roleService.findById(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getName()).isEqualTo("TEST_ROLE");

    verify(roleRepository).findById(id);
    verify(roleMapper).toInfo(role);
  }

  @Test
  void findById_shouldThrowException_whenDoesNotExist() {
    Long id = 1L;

    when(roleRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> {
      roleService.findById(id);
    });

    verify(roleRepository).findById(id);
  }

  @Test
  void create_shouldCreateRoleWithoutPermissions() {
    RoleData roleData = RoleData.builder()
      .name("NEW_ROLE")
      .description("New Role Description")
      .build();

    Role role = Role.builder()
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    Role savedRole = Role.builder()
      .id(1L)
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(role.getCreatedAt())
      .updatedAt(role.getUpdatedAt())
      .build();

    RoleInfo roleInfo = RoleInfo.builder()
      .id(1L)
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(role.getCreatedAt())
      .updatedAt(role.getUpdatedAt())
      .build();

    when(roleRepository.existsByName(roleData.getName())).thenReturn(false);
    when(roleMapper.toEntity(roleData)).thenReturn(role);
    when(roleRepository.save(role)).thenReturn(savedRole);
    when(roleMapper.toInfo(savedRole)).thenReturn(roleInfo);

    RoleInfo result = roleService.create(roleData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("NEW_ROLE");

    verify(roleRepository).existsByName(roleData.getName());
    verify(roleMapper).toEntity(roleData);
    verify(roleRepository).save(role);
    verify(roleMapper).toInfo(savedRole);
  }

  @Test
  void create_shouldCreateRoleWithPermissions() {
    Set<Long> permissionIds = Set.of(1L, 2L);

    RoleData roleData = RoleData.builder()
      .name("NEW_ROLE")
      .description("New Role Description")
      .permissionIds(permissionIds)
      .build();

    Role role = Role.builder()
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .permissions(new HashSet<>())
      .build();

    Permission permission1 = Permission.builder()
      .id(1L)
      .name("PERMISSION_1")
      .build();
    Permission permission2 = Permission.builder()
      .id(2L)
      .name("PERMISSION_2")
      .build();

    Role savedRole = Role.builder()
      .id(1L)
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(role.getCreatedAt())
      .updatedAt(role.getUpdatedAt())
      .permissions(Set.of(permission1, permission2))
      .build();

    RoleInfo roleInfo = RoleInfo.builder()
      .id(1L)
      .name("NEW_ROLE")
      .description("New Role Description")
      .createdAt(role.getCreatedAt())
      .updatedAt(role.getUpdatedAt())
      .permissions(
        Set.of(
          PermissionInfo.builder().id(1L).name("PERMISSION_1").build(),
          PermissionInfo.builder().id(2L).name("PERMISSION_2").build()
        )
      )
      .build();

    when(roleRepository.existsByName(roleData.getName())).thenReturn(false);
    when(roleMapper.toEntity(roleData)).thenReturn(role);
    when(permissionRepository.findById(1L)).thenReturn(
      Optional.of(permission1)
    );
    when(permissionRepository.findById(2L)).thenReturn(
      Optional.of(permission2)
    );
    when(roleRepository.save(role)).thenReturn(savedRole);
    when(roleMapper.toInfo(savedRole)).thenReturn(roleInfo);

    RoleInfo result = roleService.create(roleData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("NEW_ROLE");
    assertThat(result.getPermissions()).hasSize(2);

    verify(roleRepository).existsByName(roleData.getName());
    verify(roleMapper).toEntity(roleData);
    verify(permissionRepository).findById(1L);
    verify(permissionRepository).findById(2L);
    verify(roleRepository).save(role);
    verify(roleMapper).toInfo(savedRole);
  }

  @Test
  void create_shouldThrowException_whenNameExists() {
    RoleData roleData = RoleData.builder()
      .name("EXISTING_ROLE")
      .description("Role Description")
      .build();

    when(roleRepository.existsByName(roleData.getName())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> {
      roleService.create(roleData);
    });

    verify(roleRepository).existsByName(roleData.getName());
    verify(roleRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateRole() {
    Long id = 1L;
    RoleData roleData = RoleData.builder()
      .name("UPDATED_ROLE")
      .description("Updated Role Description")
      .build();

    Role existingRole = Role.builder()
      .id(id)
      .name("ORIGINAL_ROLE")
      .description("Original Role Description")
      .build();

    Role updatedRole = Role.builder()
      .id(id)
      .name("UPDATED_ROLE")
      .description("Updated Role Description")
      .build();

    RoleInfo roleInfo = RoleInfo.builder()
      .id(id)
      .name("UPDATED_ROLE")
      .description("Updated Role Description")
      .build();

    when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
    when(roleRepository.existsByName("UPDATED_ROLE")).thenReturn(false);
    when(roleRepository.save(existingRole)).thenReturn(updatedRole);
    when(roleMapper.toInfo(updatedRole)).thenReturn(roleInfo);

    RoleInfo result = roleService.update(id, roleData);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getName()).isEqualTo("UPDATED_ROLE");
    assertThat(result.getDescription()).isEqualTo("Updated Role Description");

    verify(roleRepository).findById(id);
    verify(roleMapper).updateEntityFromData(eq(existingRole), eq(roleData));
    verify(roleRepository).save(existingRole);
    verify(roleMapper).toInfo(updatedRole);
  }

  @Test
  void delete_shouldDeleteRole_whenNoUsers() {
    Long id = 1L;
    Role role = Role.builder().id(id).name("TEST_ROLE").users(Set.of()).build();

    when(roleRepository.findById(id)).thenReturn(Optional.of(role));

    roleService.delete(id);

    verify(roleRepository).findById(id);
    verify(roleRepository).delete(role);
  }

  @Test
  void delete_shouldThrowException_whenAssignedToUsers() {
    Long id = 1L;
    User user = User.builder().id(1L).email("test@example.com").build();

    Role role = Role.builder()
      .id(id)
      .name("TEST_ROLE")
      .users(Set.of(user))
      .build();

    when(roleRepository.findById(id)).thenReturn(Optional.of(role));

    assertThrows(IllegalArgumentException.class, () -> {
      roleService.delete(id);
    });

    verify(roleRepository).findById(id);
    verify(roleRepository, never()).delete(any());
  }

  @Test
  void assignToUser_shouldAssignRoleToUser() {
    Long roleId = 1L;
    Long userId = 1L;

    Role role = Role.builder().id(roleId).name("TEST_ROLE").build();

    User user = User.builder()
      .id(userId)
      .email("test@example.com")
      .roles(new HashSet<>())
      .build();

    RoleInfo roleInfo = RoleInfo.builder().id(roleId).name("TEST_ROLE").build();

    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleMapper.toInfo(role)).thenReturn(roleInfo);

    RoleInfo result = roleService.assignToUser(roleId, userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(roleId);
    assertThat(user.getRoles()).contains(role);

    verify(roleRepository).findById(roleId);
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verify(roleMapper).toInfo(role);
  }

  @Test
  void removeFromUser_shouldRemoveRoleFromUser() {
    Long roleId = 1L;
    Long userId = 1L;

    Role role = Role.builder().id(roleId).name("TEST_ROLE").build();

    Set<Role> roles = new HashSet<>();
    roles.add(role);

    User user = User.builder()
      .id(userId)
      .email("test@example.com")
      .roles(roles)
      .build();

    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    roleService.removeFromUser(roleId, userId);

    assertThat(user.getRoles()).doesNotContain(role);

    verify(roleRepository).findById(roleId);
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
  }
}
