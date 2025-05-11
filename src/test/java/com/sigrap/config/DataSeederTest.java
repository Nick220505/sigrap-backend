package com.sigrap.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.category.Category;
import com.sigrap.category.CategoryRepository;
import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.product.ProductRepository;
import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserNotificationPreferenceRepository;
import com.sigrap.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PermissionRepository permissionRepository;

  @Mock
  private UserNotificationPreferenceRepository userNotificationPreferenceRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private DataSeeder dataSeeder;

  @BeforeEach
  void setUp() {
    lenient()
      .when(passwordEncoder.encode(any()))
      .thenReturn("encoded-password");
  }

  @Test
  void testSeedCategories_WhenCategoriesEmpty() throws Exception {
    when(categoryRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(categoryRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedCategories_WhenCategoriesExist() throws Exception {
    when(categoryRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(categoryRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenProductsEmpty() throws Exception {
    when(productRepository.count()).thenReturn(0L);

    List<Category> categories = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      categories.add(Category.builder().id(i).name("Category " + i).build());
    }
    when(categoryRepository.findAll()).thenReturn(categories);

    dataSeeder.run();

    verify(productRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenProductsExist() throws Exception {
    when(productRepository.count()).thenReturn(10L);

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedProducts_WhenCategoriesNotFound() throws Exception {
    when(productRepository.count()).thenReturn(0L);
    when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(productRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedUsers_WhenUsersEmpty() throws Exception {
    when(userRepository.count()).thenReturn(0L);
    when(roleRepository.findAll()).thenReturn(createTestRoles());

    dataSeeder.run();

    verify(userRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedUsers_WhenUsersExist() throws Exception {
    when(userRepository.count()).thenReturn(2L);

    dataSeeder.run();

    verify(userRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedPermissions_WhenPermissionsEmpty() throws Exception {
    when(permissionRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(permissionRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedPermissions_WhenPermissionsExist() throws Exception {
    when(permissionRepository.count()).thenReturn(10L);

    dataSeeder.run();

    verify(permissionRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedRoles_WhenRolesEmpty() throws Exception {
    when(roleRepository.count()).thenReturn(0L);
    when(permissionRepository.findAll()).thenReturn(createTestPermissions());

    dataSeeder.run();

    verify(roleRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedRoles_WhenRolesExist() throws Exception {
    when(roleRepository.count()).thenReturn(2L);

    dataSeeder.run();

    verify(roleRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedRoles_WhenPermissionsNotFound() throws Exception {
    when(roleRepository.count()).thenReturn(0L);
    when(permissionRepository.findAll()).thenReturn(Collections.emptyList());

    dataSeeder.run();

    verify(roleRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedUserNotificationPreferences_WhenPreferencesEmptyAndUsersExist()
    throws Exception {
    when(userNotificationPreferenceRepository.count()).thenReturn(0L);
    when(userRepository.count()).thenReturn(2L);
    when(userRepository.findAll()).thenReturn(createTestUsers());

    dataSeeder.run();

    verify(userNotificationPreferenceRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testSeedUserNotificationPreferences_WhenPreferencesExist()
    throws Exception {
    when(userNotificationPreferenceRepository.count()).thenReturn(5L);

    dataSeeder.run();

    verify(userNotificationPreferenceRepository, never()).saveAll(anyList());
  }

  @Test
  void testSeedUserNotificationPreferences_WhenNoUsers() throws Exception {
    when(userNotificationPreferenceRepository.count()).thenReturn(0L);
    when(userRepository.count()).thenReturn(0L);

    dataSeeder.run();

    verify(userNotificationPreferenceRepository, never()).saveAll(anyList());
  }

  private List<Permission> createTestPermissions() {
    List<Permission> permissions = new ArrayList<>();

    permissions.add(
      Permission.builder()
        .id(1L)
        .name("USER_READ")
        .resource("USER")
        .action("READ")
        .build()
    );

    permissions.add(
      Permission.builder()
        .id(2L)
        .name("PRODUCT_READ")
        .resource("PRODUCT")
        .action("READ")
        .build()
    );

    return permissions;
  }

  private List<Role> createTestRoles() {
    List<Role> roles = new ArrayList<>();

    roles.add(
      Role.builder()
        .id(1L)
        .name("ADMIN")
        .description("Administrator role")
        .permissions(new HashSet<>())
        .build()
    );

    roles.add(
      Role.builder()
        .id(2L)
        .name("EMPLOYEE")
        .description("Employee role")
        .permissions(new HashSet<>())
        .build()
    );

    return roles;
  }

  private List<User> createTestUsers() {
    List<User> users = new ArrayList<>();

    users.add(
      User.builder()
        .id(1L)
        .name("Test Admin")
        .email("admin@test.com")
        .password("encoded-password")
        .status(User.UserStatus.ACTIVE)
        .roles(new HashSet<>())
        .build()
    );

    users.add(
      User.builder()
        .id(2L)
        .name("Test Employee")
        .email("employee@test.com")
        .password("encoded-password")
        .status(User.UserStatus.ACTIVE)
        .roles(new HashSet<>())
        .build()
    );

    return users;
  }
}
