package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JwtTokenValidatorAdapter.
 * Tests the adapter with real JwtUtil to verify end-to-end token validation.
 */
@SpringBootTest(classes = {JwtUtil.class, JwtTokenValidatorAdapter.class})
@TestPropertySource(properties = {
    "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdmFsaWRhdGlvbi10ZXN0aW5nLW11c3QtYmUtbG9uZy1lbm91Z2g=",
    "jwt.expiration=3600000"
})
class JwtTokenValidatorAdapterIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenValidatorAdapter adapter;

    private String validToken;
    private String expiredToken;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        // Generate a valid token
        UserDetails userDetails = User.builder()
            .username(testEmail)
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        validToken = jwtUtil.generateToken(userDetails);
    }

    @Test
    void shouldValidateRealTokenAndExtractEmail() {
        // When
        Email result = adapter.validateToken(validToken);

        // Then
        assertNotNull(result);
        assertEquals(testEmail, result.value());
    }

    @Test
    void shouldReturnTrueForValidRealToken() {
        // When
        boolean result = adapter.isTokenValid(validToken);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldThrowExceptionForMalformedToken() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.validateToken(malformedToken)
        );
        assertTrue(exception.getMessage().contains("Invalid or expired token"));
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean result = adapter.isTokenValid(malformedToken);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionForEmptyToken() {
        // Given
        String emptyToken = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adapter.validateToken(emptyToken));
    }

    @Test
    void shouldReturnFalseForEmptyToken() {
        // Given
        String emptyToken = "";

        // When
        boolean result = adapter.isTokenValid(emptyToken);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionForNullToken() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adapter.validateToken(null));
    }

    @Test
    void shouldReturnFalseForNullToken() {
        // When
        boolean result = adapter.isTokenValid(null);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldValidateMultipleDifferentTokens() {
        // Given
        UserDetails user1 = User.builder()
            .username("user1@example.com")
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        UserDetails user2 = User.builder()
            .username("user2@example.com")
            .password("password")
            .authorities(Collections.emptyList())
            .build();

        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        // When
        Email email1 = adapter.validateToken(token1);
        Email email2 = adapter.validateToken(token2);

        // Then
        assertEquals("user1@example.com", email1.value());
        assertEquals("user2@example.com", email2.value());
        assertTrue(adapter.isTokenValid(token1));
        assertTrue(adapter.isTokenValid(token2));
    }

    @Test
    void shouldExtractCorrectEmailFromToken() {
        // Given
        String expectedEmail = "specific.user@example.com";
        UserDetails userDetails = User.builder()
            .username(expectedEmail)
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        String token = jwtUtil.generateToken(userDetails);

        // When
        Email result = adapter.validateToken(token);

        // Then
        assertEquals(expectedEmail, result.value());
    }

    @Test
    void shouldHandleTokenWithSpecialCharactersInEmail() {
        // Given
        String emailWithSpecialChars = "user+test@example.co.uk";
        UserDetails userDetails = User.builder()
            .username(emailWithSpecialChars)
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        String token = jwtUtil.generateToken(userDetails);

        // When
        Email result = adapter.validateToken(token);

        // Then
        assertEquals(emailWithSpecialChars, result.value());
        assertTrue(adapter.isTokenValid(token));
    }

    @Test
    void shouldThrowExceptionForTamperedToken() {
        // Given - tamper with the token by changing a character
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adapter.validateToken(tamperedToken));
        assertFalse(adapter.isTokenValid(tamperedToken));
    }

    @Test
    void shouldValidateTokenGeneratedByJwtUtil() {
        // This test verifies the integration between JwtUtil and JwtTokenValidatorAdapter
        // Given
        UserDetails userDetails = User.builder()
            .username("integration@test.com")
            .password("password")
            .authorities(Collections.emptyList())
            .build();

        // When - generate token with JwtUtil
        String token = jwtUtil.generateToken(userDetails);

        // Then - validate with adapter
        Email email = adapter.validateToken(token);
        assertEquals("integration@test.com", email.value());
        assertTrue(adapter.isTokenValid(token));

        // And - verify JwtUtil can also validate it
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
}
