package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailTest {

    @Test
    void shouldCreateUserEmailWithValidValue() {
        UserEmail email = new UserEmail("user@example.com");
        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserEmail(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new UserEmail(""));
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("   "));
    }

    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("invalid"));
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("invalid@"));
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("invalid@.com"));
        assertThrows(IllegalArgumentException.class, () -> new UserEmail("invalid@example"));
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        assertDoesNotThrow(() -> new UserEmail("user@example.com"));
        assertDoesNotThrow(() -> new UserEmail("user.name@example.com"));
        assertDoesNotThrow(() -> new UserEmail("user+tag@example.co.uk"));
        assertDoesNotThrow(() -> new UserEmail("user_name@example-domain.com"));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longEmail = "a".repeat(250) + "@example.com";
        assertThrows(IllegalArgumentException.class, () -> new UserEmail(longEmail));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UserEmail email1 = new UserEmail("user@example.com");
        UserEmail email2 = new UserEmail("user@example.com");
        assertEquals(email1, email2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        UserEmail email1 = new UserEmail("user1@example.com");
        UserEmail email2 = new UserEmail("user2@example.com");
        assertNotEquals(email1, email2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        UserEmail email1 = new UserEmail("user@example.com");
        UserEmail email2 = new UserEmail("user@example.com");
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
