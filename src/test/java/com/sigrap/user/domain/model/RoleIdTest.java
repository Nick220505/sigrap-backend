package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleIdTest {

    @Test
    void shouldCreateRoleIdWithValidValue() {
        RoleId roleId = new RoleId(1L);
        assertEquals(1L, roleId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RoleId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new RoleId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new RoleId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        RoleId roleId1 = new RoleId(1L);
        RoleId roleId2 = new RoleId(1L);
        assertEquals(roleId1, roleId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        RoleId roleId1 = new RoleId(1L);
        RoleId roleId2 = new RoleId(2L);
        assertNotEquals(roleId1, roleId2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        RoleId roleId1 = new RoleId(1L);
        RoleId roleId2 = new RoleId(1L);
        assertEquals(roleId1.hashCode(), roleId2.hashCode());
    }
}
