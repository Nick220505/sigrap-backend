package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionNameTest {

    @Test
    void shouldCreatePermissionNameWithValidValue() {
        PermissionName permissionName = new PermissionName("READ_USER");
        assertEquals("READ_USER", permissionName.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionName(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionName(""));
        assertThrows(IllegalArgumentException.class, () -> new PermissionName("   "));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longPermissionName = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new PermissionName(longPermissionName));
    }

    @Test
    void shouldAcceptPermissionNameAtMaxLength() {
        String maxLengthPermissionName = "a".repeat(100);
        PermissionName permissionName = new PermissionName(maxLengthPermissionName);
        assertEquals(maxLengthPermissionName, permissionName.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        PermissionName permissionName1 = new PermissionName("READ_USER");
        PermissionName permissionName2 = new PermissionName("READ_USER");
        assertEquals(permissionName1, permissionName2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        PermissionName permissionName1 = new PermissionName("READ_USER");
        PermissionName permissionName2 = new PermissionName("WRITE_USER");
        assertNotEquals(permissionName1, permissionName2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        PermissionName permissionName1 = new PermissionName("READ_USER");
        PermissionName permissionName2 = new PermissionName("READ_USER");
        assertEquals(permissionName1.hashCode(), permissionName2.hashCode());
    }
}
