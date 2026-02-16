package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtTokenValidatorAdapter.
 * Tests the adapter with mocked JwtUtil to verify token validation logic.
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorAdapterTest {

    @Mock
    private JwtUtil jwtUtil;

    private JwtTokenValidatorAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenValidatorAdapter(jwtUtil);
    }

    @Test
    void shouldValidateTokenAndExtractEmail() {
        // Given
        String token = "valid.jwt.token";
        String expectedEmail = "user@example.com";
        when(jwtUtil.extractUsername(token)).thenReturn(expectedEmail);

        // When
        Email result = adapter.validateToken(token);

        // Then
        assertNotNull(result);
        assertEquals(expectedEmail, result.value());
        verify(jwtUtil).extractUsername(token);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String token = "invalid.token";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.validateToken(token)
        );
        assertTrue(exception.getMessage().contains("Invalid or expired token"));
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        // Given
        String token = "token.with.null.email";
        when(jwtUtil.extractUsername(token)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.validateToken(token)
        );
        assertTrue(exception.getMessage().contains("does not contain a valid email"));
    }

    @Test
    void shouldThrowExceptionForBlankEmail() {
        // Given
        String token = "token.with.blank.email";
        when(jwtUtil.extractUsername(token)).thenReturn("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.validateToken(token)
        );
        assertTrue(exception.getMessage().contains("does not contain a valid email"));
    }

    @Test
    void shouldReturnTrueForValidToken() {
        // Given
        String token = "valid.token";
        when(jwtUtil.extractUsername(token)).thenReturn("user@example.com");

        // When
        boolean result = adapter.isTokenValid(token);

        // Then
        assertTrue(result);
        verify(jwtUtil).extractUsername(token);
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String token = "invalid.token";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid"));

        // When
        boolean result = adapter.isTokenValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForTokenWithNullEmail() {
        // Given
        String token = "token.null.email";
        when(jwtUtil.extractUsername(token)).thenReturn(null);

        // When
        boolean result = adapter.isTokenValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForTokenWithBlankEmail() {
        // Given
        String token = "token.blank.email";
        when(jwtUtil.extractUsername(token)).thenReturn("");

        // When
        boolean result = adapter.isTokenValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldHandleExpiredToken() {
        // Given
        String token = "expired.token";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Token expired"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adapter.validateToken(token));
        assertFalse(adapter.isTokenValid(token));
    }

    @Test
    void shouldValidateDifferentTokens() {
        // Given
        String token1 = "token1";
        String token2 = "token2";
        when(jwtUtil.extractUsername(token1)).thenReturn("user1@example.com");
        when(jwtUtil.extractUsername(token2)).thenReturn("user2@example.com");

        // When
        Email email1 = adapter.validateToken(token1);
        Email email2 = adapter.validateToken(token2);

        // Then
        assertEquals("user1@example.com", email1.value());
        assertEquals("user2@example.com", email2.value());
        verify(jwtUtil, times(2)).extractUsername(anyString());
    }
}
