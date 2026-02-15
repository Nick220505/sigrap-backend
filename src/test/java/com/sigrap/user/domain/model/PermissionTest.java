package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldCreatePermissionWithValidData() {
        PermissionName name = new PermissionName("READ_USER");
        String resource = "USER";
        String action = "READ";

        Permission permission = new Permission(name, resource, action);

        assertNull(permission.getId());
        assertEquals(name, permission.getName());
        assertEquals(resource, permission.getResource());
        assertEquals(action, permission.getAction());
    }

    @Test
    void shouldCreatePermissionWithFullConstructor() {
        PermissionId id = new PermissionId(1L);
        PermissionName name = new PermissionName("READ_USER");
        String resource = "USER";
        String action = "READ";

        Permission permission = new Permission(id, name, resource, action);

        assertEquals(id, permission.getId());
        assertEquals(name, permission.getName());
        assertEquals(resource, permission.getResource());
        assertEquals(action, permission.getAction());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Permission(null, "USER", "READ")
        );
    }

    @Test
    void shouldThrowExceptionWhenResourceIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Permission(new PermissionName("READ_USER"), null, "READ")
        );
    }

    @Test
    void shouldThrowExceptionWhenActionIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Permission(new PermissionName("READ_USER"), "USER", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenResourceIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Permission(new PermissionName("READ_USER"), "", "READ")
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Permission(new PermissionName("READ_USER"), "   ", "READ")
        );
    }

    @Test
    void shouldThrowExceptionWhenActionIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Permission(new PermissionName("READ_USER"), "USER", "")
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Permission(new PermissionName("READ_USER"), "USER", "   ")
        );
    }

    @Test
    void shouldUpdateName() {
        Permission permission = createTestPermission();
        PermissionName newName = new PermissionName("WRITE_USER");

        permission.updateName(newName);

        assertEquals(newName, permission.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNameToNull() {
        Permission permission = createTestPermission();
        assertThrows(NullPointerException.class, () -> permission.updateName(null));
    }

    @Test
    void shouldUpdateResource() {
        Permission permission = createTestPermission();
        String newResource = "PRODUCT";

        permission.updateResource(newResource);

        assertEquals(newResource, permission.getResource());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingResourceToNull() {
        Permission permission = createTestPermission();
        assertThrows(NullPointerException.class, () -> permission.updateResource(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingResourceToBlank() {
        Permission permission = createTestPermission();
        assertThrows(IllegalArgumentException.class, () -> permission.updateResource(""));
        assertThrows(IllegalArgumentException.class, () -> permission.updateResource("   "));
    }

    @Test
    void shouldUpdateAction() {
        Permission permission = createTestPermission();
        String newAction = "WRITE";

        permission.updateAction(newAction);

        assertEquals(newAction, permission.getAction());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingActionToNull() {
        Permission permission = createTestPermission();
        assertThrows(NullPointerException.class, () -> permission.updateAction(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingActionToBlank() {
        Permission permission = createTestPermission();
        assertThrows(IllegalArgumentException.class, () -> permission.updateAction(""));
        assertThrows(IllegalArgumentException.class, () -> permission.updateAction("   "));
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        Permission permission = createTestPermission();
        assertTrue(permission.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        Permission permission = new Permission(
                new PermissionId(1L),
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );
        assertFalse(permission.isNew());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        PermissionId id = new PermissionId(1L);
        Permission permission1 = new Permission(id, new PermissionName("READ_USER"), "USER", "READ");
        Permission permission2 = new Permission(id, new PermissionName("WRITE_USER"), "PRODUCT", "WRITE");
        assertEquals(permission1, permission2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Permission permission1 = new Permission(new PermissionId(1L), new PermissionName("READ_USER"), "USER", "READ");
        Permission permission2 = new Permission(new PermissionId(2L), new PermissionName("READ_USER"), "USER", "READ");
        assertNotEquals(permission1, permission2);
    }

    @Test
    void shouldHaveValidToString() {
        Permission permission = createTestPermission();
        String toString = permission.toString();
        assertTrue(toString.contains("Permission"));
        assertTrue(toString.contains("name="));
        assertTrue(toString.contains("resource="));
        assertTrue(toString.contains("action="));
    }

    // Helper methods

    private Permission createTestPermission() {
        return new Permission(
                new PermissionName("READ_USER"),
                "USER",
                "READ"
        );
    }
}
