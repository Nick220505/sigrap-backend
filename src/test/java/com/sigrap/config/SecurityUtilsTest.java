package com.sigrap.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SecurityUtils securityUtils;

  private User adminUser;
  private User employeeUser;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();

    adminUser = User.builder()
      .id(1L)
      .email("admin@example.com")
      .name("Admin User")
      .role(UserRole.ADMINISTRATOR)
      .build();

    employeeUser = User.builder()
      .id(2L)
      .email("employee@example.com")
      .name("Employee User")
      .role(UserRole.EMPLOYEE)
      .build();
  }

  @Test
  void getCurrentAuthentication_whenAuthenticated_returnsAuthentication() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    Authentication result = securityUtils.getCurrentAuthentication();

    assertNotNull(result);
    assertEquals("admin@example.com", result.getName());
  }

  @Test
  void getCurrentAuthentication_whenNotAuthenticated_returnsNull() {
    Authentication result = securityUtils.getCurrentAuthentication();

    assertNull(result);
  }

  @Test
  void getCurrentUsername_whenAuthenticated_returnsUsername() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    String result = securityUtils.getCurrentUsername();

    assertEquals("admin@example.com", result);
  }

  @Test
  void getCurrentUsername_whenNotAuthenticated_returnsNull() {
    String result = securityUtils.getCurrentUsername();

    assertNull(result);
  }

  @Test
  void getCurrentUser_whenAuthenticated_returnsUser() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );

    Optional<User> result = securityUtils.getCurrentUser();

    assertTrue(result.isPresent());
    assertEquals(adminUser, result.get());
  }

  @Test
  void getCurrentUser_whenNotAuthenticated_returnsEmpty() {
    Optional<User> result = securityUtils.getCurrentUser();

    assertFalse(result.isPresent());
  }

  @Test
  void getCurrentUser_whenAuthenticatedButUserNotFound_returnsEmpty() {
    Authentication auth = new TestingAuthenticationToken(
      "nonexistent@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(
      Optional.empty()
    );

    Optional<User> result = securityUtils.getCurrentUser();

    assertFalse(result.isPresent());
  }

  @Test
  void isAuthenticated_whenAuthenticated_returnsTrue() {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    SecurityContextHolder.getContext().setAuthentication(auth);

    boolean result = securityUtils.isAuthenticated();

    assertTrue(result);
  }

  @Test
  void isAuthenticated_whenNotAuthenticated_returnsFalse() {
    boolean result = securityUtils.isAuthenticated();

    assertFalse(result);
  }

  @Test
  void hasRole_whenUserHasRole_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );

    boolean result = securityUtils.hasRole(UserRole.ADMINISTRATOR);

    assertTrue(result);
  }

  @Test
  void hasRole_whenUserDoesNotHaveRole_returnsFalse() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasRole(UserRole.ADMINISTRATOR);

    assertFalse(result);
  }

  @Test
  void hasRole_whenNotAuthenticated_returnsFalse() {
    boolean result = securityUtils.hasRole(UserRole.ADMINISTRATOR);

    assertFalse(result);
  }

  @Test
  void isOwner_whenUserIsOwner_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );

    boolean result = securityUtils.isOwner(123L, 1L);

    assertTrue(result);
  }

  @Test
  void isOwner_whenUserIsNotOwner_returnsFalse() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );

    boolean result = securityUtils.isOwner(123L, 2L);

    assertFalse(result);
  }

  @Test
  void isOwner_whenNotAuthenticated_returnsFalse() {
    boolean result = securityUtils.isOwner(123L, 1L);

    assertFalse(result);
  }

  @Test
  void hasPermission_whenAdministrator_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "admin@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("admin@example.com")).thenReturn(
      Optional.of(adminUser)
    );

    boolean result = securityUtils.hasPermission("Product", "DELETE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithReadAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("User", "READ");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithProductCreateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Product", "CREATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithProductUpdateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Product", "UPDATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithCategoryCreateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Category", "CREATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithCategoryUpdateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Category", "UPDATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithCustomerCreateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Customer", "CREATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithCustomerUpdateAccess_returnsTrue() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("Customer", "UPDATE");

    assertTrue(result);
  }

  @Test
  void hasPermission_whenEmployeeWithNoAccess_returnsFalse() {
    Authentication auth = new TestingAuthenticationToken(
      "employee@example.com",
      "password"
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmail("employee@example.com")).thenReturn(
      Optional.of(employeeUser)
    );

    boolean result = securityUtils.hasPermission("User", "DELETE");

    assertFalse(result);
  }

  @Test
  void hasPermission_whenNotAuthenticated_returnsFalse() {
    boolean result = securityUtils.hasPermission("Product", "READ");

    assertFalse(result);
  }
}
