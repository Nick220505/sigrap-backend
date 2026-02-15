package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionIdTest {

    @Test
    void shouldCreatePermissionIdWithValidValue() {
        PermissionId permissionId = new PermissionId(1L);
        assertEquals(1L, permissionId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        PermissionId permissionId1 = new PermissionId(1L);
        PermissionId permissionId2 = new PermissionId(1L);
        assertEquals(permissionId1, permissionId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        PermissionId permissionId1 = new PermissionId(1L);
        PermissionId permissionId2 = new PermissionId(2L);
        assertNotEquals(permissionId1, permissionId2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        PermissionId permissionId1 = new PermissionId(1L);
        PermissionId permissionId2 = new PermissionId(1L);
        assertEquals(permissionId1.hashCode(), permissionId2.hashCode());
    }
}
