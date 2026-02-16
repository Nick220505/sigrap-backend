package com.sigrap.config;

import com.sigrap.user.domain.model.*;
import com.sigrap.user.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for SecurityUtils after hexagonal architecture migration.
 * 
 * Tests that SecurityUtils correctly:
 * - Retrieves current authentication
 * - Retrieves current username
 * - Retrieves current user from repository
 * - Checks authentication status
 * - Checks user roles
 * - Checks user permissions
 */
@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private SecurityContext securityContext;

    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils(userRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetCurrentAuthentication() {
        // Given: An authenticated user
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When: Getting current authentication
        Authentication result = securityUtils.getCurrentAuthentication();

        // Then: Authentication is returned
        assertNotNull(result);
        assertEquals("test@example.com", result.getName());
    }

    @Test
    void shouldGetCurrentUsername() {
        // Given: An authenticated user
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When: Getting current username
        String username = securityUtils.getCurrentUsername();

        // Then: Username is returned
        assertEquals("test@example.com", username);
    }

    @Test
    void shouldReturnNullWhenNotAuthenticated() {
        // Given: No authentication
        when(securityContext.getAuthentication()).thenReturn(null);

        // When: Getting current username
        String username = securityUtils.getCurrentUsername();

        // Then: Null is returned
        assertNull(username);
    }

    @Test
    void shouldGetCurrentUser() {
        // Given: An authenticated user exists in repository
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createTestUser();
        when(userRepository.findByEmail(new UserEmail("test@example.com")))
                .thenReturn(Optional.of(user));

        // When: Getting current user
        Optional<User> result = securityUtils.getCurrentUser();

        // Then: User is returned
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail().value());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given: An authenticated user does not exist in repository
        Authentication authentication = createAuthentication("nonexistent@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(new UserEmail("nonexistent@example.com")))
                .thenReturn(Optional.empty());

        // When: Getting current user
        Optional<User> result = securityUtils.getCurrentUser();

        // Then: Empty is returned
        assertFalse(result.isPresent());
    }

    @Test
    void shouldCheckIfAuthenticated() {
        // Given: An authenticated user (UsernamePasswordAuthenticationToken is authenticated by default when created with authorities)
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When: Checking if authenticated
        boolean result = securityUtils.isAuthenticated();

        // Then: Returns true
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNotAuthenticated() {
        // Given: No authentication
        when(securityContext.getAuthentication()).thenReturn(null);

        // When: Checking if authenticated
        boolean result = securityUtils.isAuthenticated();

        // Then: Returns false
        assertFalse(result);
    }

    @Test
    void shouldCheckIfUserHasRole() {
        // Given: A user with ADMINISTRATOR role
        Authentication authentication = createAuthentication("admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createUserWithRole("ADMINISTRATOR");
        when(userRepository.findByEmail(new UserEmail("admin@example.com")))
                .thenReturn(Optional.of(user));

        // When: Checking if user has role
        boolean result = securityUtils.hasRole("ADMINISTRATOR");

        // Then: Returns true
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHaveRole() {
        // Given: A user with EMPLOYEE role
        Authentication authentication = createAuthentication("employee@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createUserWithRole("EMPLOYEE");
        when(userRepository.findByEmail(new UserEmail("employee@example.com")))
                .thenReturn(Optional.of(user));

        // When: Checking if user has ADMINISTRATOR role
        boolean result = securityUtils.hasRole("ADMINISTRATOR");

        // Then: Returns false
        assertFalse(result);
    }

    @Test
    void shouldCheckIfUserHasPermission() {
        // Given: An administrator user
        Authentication authentication = createAuthentication("admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createUserWithRole("ADMINISTRATOR");
        when(userRepository.findByEmail(new UserEmail("admin@example.com")))
                .thenReturn(Optional.of(user));

        // When: Checking if user has permission
        boolean result = securityUtils.hasPermission("Product", "DELETE");

        // Then: Returns true (administrators have all permissions)
        assertTrue(result);
    }

    @Test
    void shouldCheckIfUserIsOwner() {
        // Given: A user with ID 1
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createTestUser();
        when(userRepository.findByEmail(new UserEmail("test@example.com")))
                .thenReturn(Optional.of(user));

        // When: Checking if user is owner
        boolean result = securityUtils.isOwner(1L, 1L);

        // Then: Returns true
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenUserIsNotOwner() {
        // Given: A user with ID 1
        Authentication authentication = createAuthentication("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = createTestUser();
        when(userRepository.findByEmail(new UserEmail("test@example.com")))
                .thenReturn(Optional.of(user));

        // When: Checking if user is owner of entity belonging to user 2
        boolean result = securityUtils.isOwner(1L, 2L);

        // Then: Returns false
        assertFalse(result);
    }

    /**
     * Helper method to create an authentication object.
     */
    private Authentication createAuthentication(String email) {
        return new UsernamePasswordAuthenticationToken(
                email,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Helper method to create a test user.
     */
    private User createTestUser() {
        UserId userId = new UserId(1L);
        Username username = new Username("testuser");
        UserEmail email = new UserEmail("test@example.com");
        String hashedPassword = "$2a$10$dummyHashedPassword";

        Set<Role> roles = new HashSet<>();
        RoleId roleId = new RoleId(1L);
        RoleName roleName = new RoleName("EMPLOYEE");
        Role role = new Role(roleId, roleName, new HashSet<>(), "Employee role");
        roles.add(role);

        return new User(userId, username, email, hashedPassword, roles, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Helper method to create a user with a specific role.
     */
    private User createUserWithRole(String roleNameStr) {
        UserId userId = new UserId(1L);
        Username username = new Username("testuser");
        UserEmail email = new UserEmail("test@example.com");
        String hashedPassword = "$2a$10$dummyHashedPassword";

        Set<Role> roles = new HashSet<>();
        RoleId roleId = new RoleId(1L);
        RoleName roleName = new RoleName(roleNameStr);
        Role role = new Role(roleId, roleName, new HashSet<>(), roleNameStr + " role");
        roles.add(role);

        return new User(userId, username, email, hashedPassword, roles, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
