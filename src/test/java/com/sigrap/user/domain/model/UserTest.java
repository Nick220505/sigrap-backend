package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        Username username = new Username("john_doe");
        UserEmail email = new UserEmail("john@example.com");
        String hashedPassword = "hashed_password_123";

        User user = new User(username, email, hashedPassword);

        assertNull(user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getHashedPassword());
        assertTrue(user.getRoles().isEmpty());
        assertTrue(user.isEnabled());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void shouldCreateUserWithFullConstructor() {
        UserId id = new UserId(1L);
        Username username = new Username("john_doe");
        UserEmail email = new UserEmail("john@example.com");
        String hashedPassword = "hashed_password_123";
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(new RoleName("ADMIN"), "Administrator"));
        boolean enabled = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        User user = new User(id, username, email, hashedPassword, roles, enabled, createdAt, updatedAt);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getHashedPassword());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(NullPointerException.class, () ->
                new User(null, new UserEmail("john@example.com"), "password")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(NullPointerException.class, () ->
                new User(new Username("john_doe"), null, "password")
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThrows(NullPointerException.class, () ->
                new User(new Username("john_doe"), new UserEmail("john@example.com"), null)
        );
    }

    @Test
    void shouldUpdateEmail() {
        User user = createTestUser();
        UserEmail newEmail = new UserEmail("newemail@example.com");

        user.updateEmail(newEmail);

        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void shouldNotUpdateEmailWhenSameEmail() {
        User user = createTestUser();
        UserEmail sameEmail = new UserEmail("john@example.com");
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        user.updateEmail(sameEmail);

        assertEquals(sameEmail, user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmailToNull() {
        User user = createTestUser();
        assertThrows(NullPointerException.class, () -> user.updateEmail(null));
    }

    @Test
    void shouldUpdatePassword() {
        User user = createTestUser();
        String newHashedPassword = "new_hashed_password";

        user.updatePassword(newHashedPassword);

        assertEquals(newHashedPassword, user.getHashedPassword());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordToNull() {
        User user = createTestUser();
        assertThrows(NullPointerException.class, () -> user.updatePassword(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordToBlank() {
        User user = createTestUser();
        assertThrows(IllegalArgumentException.class, () -> user.updatePassword(""));
        assertThrows(IllegalArgumentException.class, () -> user.updatePassword("   "));
    }

    @Test
    void shouldEnableUser() {
        User user = createDisabledUser();

        user.enable();

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldNotChangeWhenEnablingAlreadyEnabledUser() {
        User user = createTestUser();
        assertTrue(user.isEnabled());

        user.enable();

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldDisableUser() {
        User user = createTestUser();

        user.disable();

        assertFalse(user.isEnabled());
    }

    @Test
    void shouldNotChangeWhenDisablingAlreadyDisabledUser() {
        User user = createDisabledUser();
        assertFalse(user.isEnabled());

        user.disable();

        assertFalse(user.isEnabled());
    }

    @Test
    void shouldAddRole() {
        User user = createTestUser();
        Role role = new Role(new RoleName("ADMIN"), "Administrator");

        user.addRole(role);

        assertEquals(1, user.getRoles().size());
        assertTrue(user.hasRole(role));
    }

    @Test
    void shouldNotAddDuplicateRole() {
        User user = createTestUser();
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");

        user.addRole(role);
        user.addRole(role);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullRole() {
        User user = createTestUser();
        assertThrows(NullPointerException.class, () -> user.addRole(null));
    }

    @Test
    void shouldRemoveRole() {
        User user = createTestUser();
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");
        user.addRole(role);
        assertEquals(1, user.getRoles().size());

        user.removeRole(role);

        assertEquals(0, user.getRoles().size());
        assertFalse(user.hasRole(role));
    }

    @Test
    void shouldNotChangeWhenRemovingNonExistentRole() {
        User user = createTestUser();
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");

        user.removeRole(role);

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNullRole() {
        User user = createTestUser();
        assertThrows(NullPointerException.class, () -> user.removeRole(null));
    }

    @Test
    void shouldReturnTrueWhenUserHasRole() {
        User user = createTestUser();
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");
        user.addRole(role);

        assertTrue(user.hasRole(role));
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHaveRole() {
        User user = createTestUser();
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");

        assertFalse(user.hasRole(role));
    }

    @Test
    void shouldReturnTrueWhenUserHasPermission() {
        User user = createTestUser();
        Permission permission = new Permission(new PermissionId(1L), new PermissionName("READ_USER"), "USER", "READ");
        Role role = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Administrator");
        role.addPermission(permission);
        user.addRole(role);

        assertTrue(user.hasPermission(permission));
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHavePermission() {
        User user = createTestUser();
        Permission permission = new Permission(new PermissionId(1L), new PermissionName("READ_USER"), "USER", "READ");

        assertFalse(user.hasPermission(permission));
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        User user = createTestUser();
        assertTrue(user.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        User user = new User(
                new UserId(1L),
                new Username("john_doe"),
                new UserEmail("john@example.com"),
                "hashed_password",
                new HashSet<>(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertFalse(user.isNew());
    }

    @Test
    void shouldReturnUnmodifiableSetOfRoles() {
        User user = createTestUser();
        Role role = new Role(new RoleName("ADMIN"), "Administrator");
        user.addRole(role);

        assertThrows(UnsupportedOperationException.class, () ->
                user.getRoles().add(new Role(new RoleName("USER"), "User"))
        );
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        UserId id = new UserId(1L);
        User user1 = new User(id, new Username("john_doe"), new UserEmail("john@example.com"),
                "password", new HashSet<>(), true, LocalDateTime.now(), LocalDateTime.now());
        User user2 = new User(id, new Username("jane_doe"), new UserEmail("jane@example.com"),
                "password", new HashSet<>(), true, LocalDateTime.now(), LocalDateTime.now());
        assertEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        User user1 = new User(new UserId(1L), new Username("john_doe"), new UserEmail("john@example.com"),
                "password", new HashSet<>(), true, LocalDateTime.now(), LocalDateTime.now());
        User user2 = new User(new UserId(2L), new Username("john_doe"), new UserEmail("john@example.com"),
                "password", new HashSet<>(), true, LocalDateTime.now(), LocalDateTime.now());
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldHaveValidToString() {
        User user = createTestUser();
        String toString = user.toString();
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("username="));
        assertTrue(toString.contains("email="));
        assertTrue(toString.contains("enabled="));
    }

    // Helper methods

    private User createTestUser() {
        return new User(
                new Username("john_doe"),
                new UserEmail("john@example.com"),
                "hashed_password_123"
        );
    }

    private User createDisabledUser() {
        return new User(
                null,
                new Username("john_doe"),
                new UserEmail("john@example.com"),
                "hashed_password_123",
                new HashSet<>(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
