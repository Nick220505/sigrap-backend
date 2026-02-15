package com.sigrap.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateUserIdWithValidValue() {
        UserId userId = new UserId(1L);
        assertEquals(1L, userId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        UserId userId1 = new UserId(1L);
        UserId userId2 = new UserId(1L);
        assertEquals(userId1, userId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        UserId userId1 = new UserId(1L);
        UserId userId2 = new UserId(2L);
        assertNotEquals(userId1, userId2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        UserId userId1 = new UserId(1L);
        UserId userId2 = new UserId(1L);
        assertEquals(userId1.hashCode(), userId2.hashCode());
    }
}
