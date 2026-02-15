package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtTokenGeneratorAdapter.
 * Tests the adapter with mocked JwtUtil to verify token generation logic.
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenGeneratorAdapterTest {

    @Mock
    private JwtUtil jwtUtil;

    private JwtTokenGeneratorAdapter adapter;
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenGeneratorAdapter(jwtUtil, EXPIRATION_MS);
    }

    @Test
    void shouldGenerateTokenForValidEmail() {
        // Given
        Email email = new Email("user@example.com");
        String expectedToken = "generated.jwt.token";
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // When
        JwtToken result = adapter.generateToken(email);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.value());
        assertNotNull(result.expiresAt());
        assertTrue(result.expiresAt().isAfter(LocalDateTime.now()));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void shouldGenerateTokenWithCorrectExpiration() {
        // Given
        Email email = new Email("test@example.com");
        String expectedToken = "test.token";
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedToken);
        LocalDateTime beforeGeneration = LocalDateTime.now();

        // When
        JwtToken result = adapter.generateToken(email);

        // Then
        LocalDateTime expectedExpiration = beforeGeneration.plusSeconds(EXPIRATION_MS / 1000);
        assertNotNull(result.expiresAt());
        // Allow 1 second tolerance for test execution time
        assertTrue(result.expiresAt().isAfter(expectedExpiration.minusSeconds(1)));
        assertTrue(result.expiresAt().isBefore(expectedExpiration.plusSeconds(1)));
    }

    @Test
    void shouldPassEmailToJwtUtil() {
        // Given
        Email email = new Email("verify@example.com");
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("token");

        // When
        adapter.generateToken(email);

        // Then
        verify(jwtUtil).generateToken(argThat(userDetails ->
            userDetails.getUsername().equals("verify@example.com")
        ));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentEmails() {
        // Given
        Email email1 = new Email("user1@example.com");
        Email email2 = new Email("user2@example.com");
        when(jwtUtil.generateToken(any(UserDetails.class)))
            .thenReturn("token1")
            .thenReturn("token2");

        // When
        JwtToken token1 = adapter.generateToken(email1);
        JwtToken token2 = adapter.generateToken(email2);

        // Then
        assertNotEquals(token1.value(), token2.value());
        verify(jwtUtil, times(2)).generateToken(any(UserDetails.class));
    }

    @Test
    void shouldHandleJwtUtilException() {
        // Given
        Email email = new Email("error@example.com");
        when(jwtUtil.generateToken(any(UserDetails.class)))
            .thenThrow(new RuntimeException("Token generation failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> adapter.generateToken(email));
    }
}
