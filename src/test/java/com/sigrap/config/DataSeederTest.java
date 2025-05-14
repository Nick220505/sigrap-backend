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
import com.sigrap.employee.ActivityLogRepository;
import com.sigrap.employee.AttendanceRepository;
import com.sigrap.employee.EmployeePerformanceRepository;
import com.sigrap.employee.EmployeeRepository;
import com.sigrap.employee.ScheduleRepository;
import com.sigrap.permission.Permission;
import com.sigrap.permission.PermissionRepository;
import com.sigrap.product.ProductRepository;
import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import com.sigrap.supplier.SupplierRepository;
import com.sigrap.user.User;
import com.sigrap.user.UserNotificationPreferenceRepository;
import com.sigrap.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private EmployeePerformanceRepository employeePerformanceRepository;

  @Mock
  private ActivityLogRepository activityLogRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @InjectMocks
  private DataSeeder dataSeeder;

  @BeforeEach
  void setUp() {
    lenient()
      .when(passwordEncoder.encode(any()))
      .thenReturn("encoded-password");

    lenient().when(employeeRepository.count()).thenReturn(2L);

    lenient().when(scheduleRepository.count()).thenReturn(2L);
    lenient().when(attendanceRepository.count()).thenReturn(2L);
    lenient().when(employeePerformanceRepository.count()).thenReturn(2L);
    lenient().when(activityLogRepository.count()).thenReturn(2L);
    lenient().when(supplierRepository.count()).thenReturn(2L);

    User mockAdminUser = User.builder()
      .id(1L)
      .email("rosita@sigrap.com")
      .name("Rosita González")
      .password("encoded-password")
      .status(User.UserStatus.ACTIVE)
      .build();
    lenient()
      .when(userRepository.findByEmail("rosita@sigrap.com"))
      .thenReturn(Optional.of(mockAdminUser));

    User mockEmployeeUser = User.builder()
      .id(2L)
      .email("gladys@sigrap.com")
      .name("Gladys Mendoza")
      .password("encoded-password")
      .status(User.UserStatus.ACTIVE)
      .build();
    lenient()
      .when(userRepository.findByEmail("gladys@sigrap.com"))
      .thenReturn(Optional.of(mockEmployeeUser));
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
      categories.add(
        Category.builder().id(Long.valueOf(i)).name("Category " + i).build()
      );
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
        .permissions(new HashSet<>(createTestPermissions()))
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
    Role adminRole = createTestRoles().get(0);
    Set<Role> roles = new HashSet<>();
    roles.add(adminRole);

    users.add(
      User.builder()
        .id(1L)
        .email("admin@example.com")
        .name("Admin User")
        .roles(roles)
        .status(User.UserStatus.ACTIVE)
        .password("encoded-password")
        .build()
    );

    users.add(
      User.builder()
        .id(2L)
        .email("user@example.com")
        .name("Regular User")
        .roles(roles)
        .status(User.UserStatus.ACTIVE)
        .password("encoded-password")
        .build()
    );

    return users;
  }
}
