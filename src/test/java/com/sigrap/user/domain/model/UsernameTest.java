package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void shouldCreateUsernameWithValidValue() {
        Username username = new Username("john_doe");
        assertEquals("john_doe", username.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Username(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Username(""));
        assertThrows(IllegalArgumentException.class, () -> new Username("   "));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longUsername = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new Username(longUsername));
    }

    @Test
    void shouldAcceptUsernameAtMaxLength() {
        String maxLengthUsername = "a".repeat(100);
        Username username = new Username(maxLengthUsername);
        assertEquals(maxLengthUsername, username.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        Username username1 = new Username("john_doe");
        Username username2 = new Username("john_doe");
        assertEquals(username1, username2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        Username username1 = new Username("john_doe");
        Username username2 = new Username("jane_doe");
        assertNotEquals(username1, username2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        Username username1 = new Username("john_doe");
        Username username2 = new Username("john_doe");
        assertEquals(username1.hashCode(), username2.hashCode());
    }
}
