package com.sigrap.auth.infrastructure.adapter.out.security;

import com.sigrap.auth.domain.model.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpringPasswordEncoderAdapter.
 * Tests BCrypt password encoding and validation without mocking.
 */
class SpringPasswordEncoderAdapterTest {

    private SpringPasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringPasswordEncoderAdapter();
    }

    @Test
    void shouldEncodePassword() {
        // Given
        Password password = new Password("MySecurePassword123!");

        // When
        String encoded = adapter.encode(password);

        // Then
        assertNotNull(encoded);
        assertNotEquals(password.value(), encoded);
        assertTrue(encoded.startsWith("$2a$")); // BCrypt prefix
        assertTrue(encoded.length() > 50); // BCrypt hashes are typically 60 chars
    }

    @Test
    void shouldEncodeSamePasswordDifferently() {
        // Given
        Password password = new Password("SamePassword123");

        // When
        String encoded1 = adapter.encode(password);
        String encoded2 = adapter.encode(password);

        // Then
        assertNotEquals(encoded1, encoded2); // BCrypt uses random salt
    }

    @Test
    void shouldMatchCorrectPassword() {
        // Given
        Password password = new Password("CorrectPassword123");
        String encoded = adapter.encode(password);

        // When
        boolean matches = adapter.matches(password, encoded);

        // Then
        assertTrue(matches);
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        // Given
        Password correctPassword = new Password("CorrectPassword123");
        Password wrongPassword = new Password("WrongPassword456");
        String encoded = adapter.encode(correctPassword);

        // When
        boolean matches = adapter.matches(wrongPassword, encoded);

        // Then
        assertFalse(matches);
    }

    @Test
    void shouldNotMatchWithDifferentCase() {
        // Given
        Password password = new Password("Password123");
        Password differentCase = new Password("password123");
        String encoded = adapter.encode(password);

        // When
        boolean matches = adapter.matches(differentCase, encoded);

        // Then
        assertFalse(matches); // BCrypt is case-sensitive
    }

    @Test
    void shouldHandleShortPassword() {
        // Given
        Password shortPassword = new Password("Pass1!");

        // When
        String encoded = adapter.encode(shortPassword);
        boolean matches = adapter.matches(shortPassword, encoded);

        // Then
        assertNotNull(encoded);
        assertTrue(matches);
    }

    @Test
    void shouldHandleLongPassword() {
        // Given
        String longPasswordValue = "a".repeat(100) + "1!";
        Password longPassword = new Password(longPasswordValue);

        // When
        String encoded = adapter.encode(longPassword);
        boolean matches = adapter.matches(longPassword, encoded);

        // Then
        assertNotNull(encoded);
        assertTrue(matches);
    }

    @Test
    void shouldHandlePasswordWithSpecialCharacters() {
        // Given
        Password password = new Password("P@ssw0rd!#$%^&*()");

        // When
        String encoded = adapter.encode(password);
        boolean matches = adapter.matches(password, encoded);

        // Then
        assertNotNull(encoded);
        assertTrue(matches);
    }

    @Test
    void shouldHandlePasswordWithSpaces() {
        // Given
        Password password = new Password("Pass word 123!");

        // When
        String encoded = adapter.encode(password);
        boolean matches = adapter.matches(password, encoded);

        // Then
        assertNotNull(encoded);
        assertTrue(matches);
    }

    @Test
    void shouldHandlePasswordWithUnicodeCharacters() {
        // Given
        Password password = new Password("Pässwörd123!");

        // When
        String encoded = adapter.encode(password);
        boolean matches = adapter.matches(password, encoded);

        // Then
        assertNotNull(encoded);
        assertTrue(matches);
    }

    @Test
    void shouldNotMatchEmptyStringAgainstEncodedPassword() {
        // Given
        Password password = new Password("Password123!");
        String encoded = adapter.encode(password);
        Password emptyPassword = new Password("A1!"); // Minimum valid password

        // When
        boolean matches = adapter.matches(emptyPassword, encoded);

        // Then
        assertFalse(matches);
    }

    @Test
    void shouldHandleMultipleEncodingsAndValidations() {
        // Given
        Password password1 = new Password("Password1!");
        Password password2 = new Password("Password2!");
        Password password3 = new Password("Password3!");

        // When
        String encoded1 = adapter.encode(password1);
        String encoded2 = adapter.encode(password2);
        String encoded3 = adapter.encode(password3);

        // Then
        assertTrue(adapter.matches(password1, encoded1));
        assertTrue(adapter.matches(password2, encoded2));
        assertTrue(adapter.matches(password3, encoded3));
        assertFalse(adapter.matches(password1, encoded2));
        assertFalse(adapter.matches(password2, encoded3));
        assertFalse(adapter.matches(password3, encoded1));
    }

    @Test
    void shouldProduceDifferentHashesForSimilarPasswords() {
        // Given
        Password password1 = new Password("Password123!");
        Password password2 = new Password("Password124!");

        // When
        String encoded1 = adapter.encode(password1);
        String encoded2 = adapter.encode(password2);

        // Then
        assertNotEquals(encoded1, encoded2);
        assertTrue(adapter.matches(password1, encoded1));
        assertTrue(adapter.matches(password2, encoded2));
        assertFalse(adapter.matches(password1, encoded2));
        assertFalse(adapter.matches(password2, encoded1));
    }
}
