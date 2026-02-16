package com.sigrap.config;

import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for CustomPermissionEvaluator after hexagonal architecture migration.
 * 
 * Tests that the permission evaluator correctly:
 * - Grants administrators access to all resources
 * - Grants employees read access to all resources
 * - Grants employees create/update access to specific resources
 * - Denies access when appropriate
 */
@ExtendWith(MockitoExtension.class)
class CustomPermissionEvaluatorTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private Authentication authentication;

    private CustomPermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setUp() {
        permissionEvaluator = new CustomPermissionEvaluator(userRepository);
    }

    @Test
    void shouldGrantAdministratorAccessToAllResources() {
        // Given: An administrator user
        User adminUser = createUserWithRole("ADMINISTRATOR");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail(new UserEmail("admin@example.com")))
                .thenReturn(Optional.of(adminUser));

        // When/Then: Administrator has access to all resources and actions
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "READ"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "CREATE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "UPDATE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "DELETE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "User", "DELETE"));
    }

    @Test
    void shouldGrantEmployeeReadAccessToAllResources() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee has read access to all resources
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "READ"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Category", "READ"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Customer", "READ"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "User", "READ"));
    }

    @Test
    void shouldGrantEmployeeCreateUpdateAccessToProducts() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee has create/update access to products
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "CREATE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Product", "UPDATE"));
    }

    @Test
    void shouldGrantEmployeeCreateUpdateAccessToCategories() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee has create/update access to categories
        assertTrue(permissionEvaluator.hasPermission(authentication, "Category", "CREATE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Category", "UPDATE"));
    }

    @Test
    void shouldGrantEmployeeCreateUpdateAccessToCustomers() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee has create/update access to customers
        assertTrue(permissionEvaluator.hasPermission(authentication, "Customer", "CREATE"));
        assertTrue(permissionEvaluator.hasPermission(authentication, "Customer", "UPDATE"));
    }

    @Test
    void shouldDenyEmployeeDeleteAccessToProducts() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee does not have delete access to products
        assertFalse(permissionEvaluator.hasPermission(authentication, "Product", "DELETE"));
    }

    @Test
    void shouldDenyEmployeeAccessToUserManagement() {
        // Given: An employee user
        User employeeUser = createUserWithRole("EMPLOYEE");
        when(authentication.getName()).thenReturn("employee@example.com");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(employeeUser));

        // When/Then: Employee does not have create/update/delete access to users
        assertFalse(permissionEvaluator.hasPermission(authentication, "User", "CREATE"));
        assertFalse(permissionEvaluator.hasPermission(authentication, "User", "UPDATE"));
        assertFalse(permissionEvaluator.hasPermission(authentication, "User", "DELETE"));
    }

    @Test
    void shouldDenyAccessWhenUserNotFound() {
        // Given: User not found in repository
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail(new UserEmail("nonexistent@example.com")))
                .thenReturn(Optional.empty());

        // When/Then: Access is denied
        assertFalse(permissionEvaluator.hasPermission(authentication, "Product", "READ"));
    }

    @Test
    void shouldDenyAccessWhenAuthenticationIsNull() {
        // When/Then: Access is denied when authentication is null
        assertFalse(permissionEvaluator.hasPermission(null, "Product", "READ"));
    }

    @Test
    void shouldDenyAccessWhenTargetIsNull() {
        // When/Then: Access is denied when target is null
        assertFalse(permissionEvaluator.hasPermission(authentication, null, "READ"));
        
        // Verify no repository interaction when target is null
        verify(userRepository, never()).findByEmail(any());
        verify(authentication, never()).getName();
    }

    @Test
    void shouldDenyAccessWhenPermissionIsNull() {
        // When/Then: Access is denied when permission is null
        assertFalse(permissionEvaluator.hasPermission(authentication, "Product", null));
        
        // Verify no repository interaction when permission is null
        verify(userRepository, never()).findByEmail(any());
        verify(authentication, never()).getName();
    }

    /**
     * Helper method to create a user with a specific role.
     */
    private User createUserWithRole(String roleName) {
        UserId userId = new UserId(1L);
        Username username = new Username("testuser");
        UserEmail email = new UserEmail("test@example.com");
        String hashedPassword = "$2a$10$dummyHashedPassword";

        Set<Role> roles = new HashSet<>();
        RoleId roleId = new RoleId(1L);
        RoleName roleNameObj = new RoleName(roleName);
        Role role = new Role(roleId, roleNameObj, new HashSet<>(), roleName + " role");
        roles.add(role);

        return new User(userId, username, email, hashedPassword, roles, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
