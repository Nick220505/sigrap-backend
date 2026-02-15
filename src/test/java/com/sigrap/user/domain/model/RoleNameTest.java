package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleNameTest {

    @Test
    void shouldCreateRoleNameWithValidValue() {
        RoleName roleName = new RoleName("ADMIN");
        assertEquals("ADMIN", roleName.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RoleName(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new RoleName(""));
        assertThrows(IllegalArgumentException.class, () -> new RoleName("   "));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longRoleName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new RoleName(longRoleName));
    }

    @Test
    void shouldAcceptRoleNameAtMaxLength() {
        String maxLengthRoleName = "a".repeat(50);
        RoleName roleName = new RoleName(maxLengthRoleName);
        assertEquals(maxLengthRoleName, roleName.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        RoleName roleName1 = new RoleName("ADMIN");
        RoleName roleName2 = new RoleName("ADMIN");
        assertEquals(roleName1, roleName2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        RoleName roleName1 = new RoleName("ADMIN");
        RoleName roleName2 = new RoleName("USER");
        assertNotEquals(roleName1, roleName2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        RoleName roleName1 = new RoleName("ADMIN");
        RoleName roleName2 = new RoleName("ADMIN");
        assertEquals(roleName1.hashCode(), roleName2.hashCode());
    }
}
