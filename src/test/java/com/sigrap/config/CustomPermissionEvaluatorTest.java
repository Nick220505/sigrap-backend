package com.sigrap.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class CustomPermissionEvaluatorTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomPermissionEvaluator permissionEvaluator;

  private Authentication adminAuthentication;
  private Authentication employeeAuthentication;
  private Authentication invalidAuthentication;

  @BeforeEach
  void setUp() {
    Collection<GrantedAuthority> adminAuthorities = List.of(
      new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")
    );
    adminAuthentication = new UsernamePasswordAuthenticationToken(
      "admin@example.com",
      "password",
      adminAuthorities
    );

    Collection<GrantedAuthority> employeeAuthorities = List.of(
      new SimpleGrantedAuthority("ROLE_EMPLOYEE")
    );
    employeeAuthentication = new UsernamePasswordAuthenticationToken(
      "employee@example.com",
      "password",
      employeeAuthorities
    );

    invalidAuthentication = new UsernamePasswordAuthenticationToken(
      "invalid@example.com",
      "password"
    );

    User adminUser = User.builder()
      .email("admin@example.com")
      .role(UserRole.ADMINISTRATOR)
      .build();

    User employeeUser = User.builder()
      .email("employee@example.com")
      .role(UserRole.EMPLOYEE)
      .build();

    lenient()
      .when(userRepository.findByEmail("admin@example.com"))
      .thenReturn(Optional.of(adminUser));
    lenient()
      .when(userRepository.findByEmail("employee@example.com"))
      .thenReturn(Optional.of(employeeUser));
    lenient()
      .when(userRepository.findByEmail("invalid@example.com"))
      .thenReturn(Optional.empty());
  }

  @Test
  void hasPermission_withNullAuthentication_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      null,
      "targetObject",
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullTargetObject_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      null,
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullPermission_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      "targetObject",
      null
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withAdminUser_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      new DummyEntity(),
      "READ"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndReadPermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new DummyEntity(),
      "READ"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndProductCreatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Product(),
      "CREATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndProductUpdatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Product(),
      "UPDATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndCategoryCreatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Category(),
      "CREATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndCategoryUpdatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Category(),
      "UPDATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndCustomerCreatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Customer(),
      "CREATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndCustomerUpdatePermission_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new Customer(),
      "UPDATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndInvalidPermission_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      new DummyEntity(),
      "DELETE"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withInvalidUser_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      invalidAuthentication,
      new DummyEntity(),
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullAuthenticationAndTargetId_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      null,
      1L,
      "Product",
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullTargetId_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      null,
      "Product",
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullTargetType_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      1L,
      null,
      "READ"
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withNullPermissionAndTargetId_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      1L,
      "Product",
      null
    );
    assertFalse(result);
  }

  @Test
  void hasPermission_withAdminUserAndTargetId_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      adminAuthentication,
      1L,
      "Product",
      "READ"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndReadPermissionAndTargetId_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      1L,
      "Product",
      "READ"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndProductCreatePermissionAndTargetId_returnsTrue() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      1L,
      "Product",
      "CREATE"
    );
    assertTrue(result);
  }

  @Test
  void hasPermission_withEmployeeUserAndInvalidPermissionAndTargetId_returnsFalse() {
    boolean result = permissionEvaluator.hasPermission(
      employeeAuthentication,
      1L,
      "Product",
      "DELETE"
    );
    assertFalse(result);
  }

  @Test
  void hasAnyRole_withNullAuthentication_returnsFalse() {
    boolean result = permissionEvaluator.hasAnyRole(null, "ADMINISTRATOR");
    assertFalse(result);
  }

  @Test
  void hasAnyRole_withNullRoles_returnsFalse() {
    boolean result = permissionEvaluator.hasAnyRole(
      adminAuthentication,
      (String[]) null
    );
    assertFalse(result);
  }

  @Test
  void hasAnyRole_withEmptyRoles_returnsFalse() {
    boolean result = permissionEvaluator.hasAnyRole(adminAuthentication);
    assertFalse(result);
  }

  @Test
  void hasAnyRole_withMatchingRole_returnsTrue() {
    boolean result = permissionEvaluator.hasAnyRole(
      adminAuthentication,
      "ADMINISTRATOR"
    );
    assertTrue(result);
  }

  @Test
  void hasAnyRole_withNonMatchingRole_returnsFalse() {
    boolean result = permissionEvaluator.hasAnyRole(
      adminAuthentication,
      "USER"
    );
    assertFalse(result);
  }

  @Test
  void hasAnyRole_withMultipleRolesIncludingMatch_returnsTrue() {
    boolean result = permissionEvaluator.hasAnyRole(
      adminAuthentication,
      "USER",
      "ADMINISTRATOR"
    );
    assertTrue(result);
  }

  private static class DummyEntity {}

  private static class Product {}

  private static class Category {}

  private static class Customer {}
}
