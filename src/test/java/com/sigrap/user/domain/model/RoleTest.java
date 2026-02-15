package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateRoleWithValidData() {
        RoleName name = new RoleName("ADMIN");
        String description = "Administrator role";

        Role role = new Role(name, description);

        assertNull(role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    void shouldCreateRoleWithFullConstructor() {
        RoleId id = new RoleId(1L);
        RoleName name = new RoleName("ADMIN");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(new Permission(new PermissionName("READ_USER"), "USER", "READ"));
        String description = "Administrator role";

        Role role = new Role(id, name, permissions, description);

        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(1, role.getPermissions().size());
        assertEquals(description, role.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Role(null, "Description")
        );
    }

    @Test
    void shouldUpdateName() {
        Role role = createTestRole();
        RoleName newName = new RoleName("SUPER_ADMIN");

        role.updateName(newName);

        assertEquals(newName, role.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNameToNull() {
        Role role = createTestRole();
        assertThrows(NullPointerException.class, () -> role.updateName(null));
    }

    @Test
    void shouldUpdateDescription() {
        Role role = createTestRole();
        String newDescription = "Updated description";

        role.updateDescription(newDescription);

        assertEquals(newDescription, role.getDescription());
    }

    @Test
    void shouldAllowNullDescription() {
        Role role = createTestRole();
        role.updateDescription(null);
        assertNull(role.getDescription());
    }

    @Test
    void shouldAddPermission() {
        Role role = createTestRole();
        Permission permission = new Permission(new PermissionName("READ_USER"), "USER", "READ");

        role.addPermission(permission);

        assertEquals(1, role.getPermissions().size());
        assertTrue(role.hasPermission(permission));
    }

    @Test
    void shouldNotAddDuplicatePermission() {
        Role role = createTestRole();
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );

        role.addPermission(permission);
        role.addPermission(permission);

        assertEquals(1, role.getPermissions().size());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullPermission() {
        Role role = createTestRole();
        assertThrows(NullPointerException.class, () -> role.addPermission(null));
    }

    @Test
    void shouldRemovePermission() {
        Role role = createTestRole();
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );
        role.addPermission(permission);
        assertEquals(1, role.getPermissions().size());

        role.removePermission(permission);

        assertEquals(0, role.getPermissions().size());
        assertFalse(role.hasPermission(permission));
    }

    @Test
    void shouldNotChangeWhenRemovingNonExistentPermission() {
        Role role = createTestRole();
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );

        role.removePermission(permission);

        assertEquals(0, role.getPermissions().size());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNullPermission() {
        Role role = createTestRole();
        assertThrows(NullPointerException.class, () -> role.removePermission(null));
    }

    @Test
    void shouldReturnTrueWhenRoleHasPermission() {
        Role role = createTestRole();
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );
        role.addPermission(permission);

        assertTrue(role.hasPermission(permission));
    }

    @Test
    void shouldReturnFalseWhenRoleDoesNotHavePermission() {
        Role role = createTestRole();
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );

        assertFalse(role.hasPermission(permission));
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        Role role = createTestRole();
        assertTrue(role.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        Role role = new Role(
                new RoleId(1L),
                new RoleName("ADMIN"),
                new HashSet<>(),
                "Administrator"
        );
        assertFalse(role.isNew());
    }

    @Test
    void shouldReturnUnmodifiableSetOfPermissions() {
        Role role = createTestRole();
        Permission permission = new Permission(new PermissionName("READ_USER"), "USER", "READ");
        role.addPermission(permission);

        assertThrows(UnsupportedOperationException.class, () ->
                role.getPermissions().add(new Permission(new PermissionName("WRITE_USER"), "USER", "WRITE"))
        );
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        RoleId id = new RoleId(1L);
        Role role1 = new Role(id, new RoleName("ADMIN"), new HashSet<>(), "Admin");
        Role role2 = new Role(id, new RoleName("USER"), new HashSet<>(), "User");
        assertEquals(role1, role2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Role role1 = new Role(new RoleId(1L), new RoleName("ADMIN"), new HashSet<>(), "Admin");
        Role role2 = new Role(new RoleId(2L), new RoleName("ADMIN"), new HashSet<>(), "Admin");
        assertNotEquals(role1, role2);
    }

    @Test
    void shouldHaveValidToString() {
        Role role = createTestRole();
        String toString = role.toString();
        assertTrue(toString.contains("Role"));
        assertTrue(toString.contains("name="));
        assertTrue(toString.contains("permissionCount="));
    }

    // Helper methods

    private Role createTestRole() {
        return new Role(new RoleName("ADMIN"), "Administrator role");
    }
}
