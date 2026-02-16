package com.sigrap.auth.infrastructure.adapter.out.jwt;

import com.sigrap.auth.infrastructure.adapter.out.jwt.JwtUtil;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for JwtTokenGeneratorAdapter with real JwtUtil.
 * Verifies end-to-end token generation without mocking.
 */
class JwtTokenGeneratorAdapterIntegrationTest {

    private JwtTokenGeneratorAdapter adapter;
    private JwtUtil jwtUtil;
    
    // Test configuration
    private static final String TEST_SECRET = "c2lncmFwLWRlZmF1bHQtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seS1kb25vdHVzZWlucHJvZHVjdGlvbg==";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        // Create real JwtUtil instance
        jwtUtil = new JwtUtil();
        
        // Set properties using reflection (simulates @Value injection)
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION_MS);
        
        // Create adapter with real JwtUtil
        adapter = new JwtTokenGeneratorAdapter(jwtUtil, EXPIRATION_MS);
    }

    @Test
    void shouldGenerateValidJwtToken() {
        // Given
        Email email = new Email("test@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then
        assertNotNull(token);
        assertNotNull(token.value());
        assertFalse(token.value().isBlank());
        assertNotNull(token.expiresAt());
        assertTrue(token.isValid());
        assertFalse(token.isExpired());
    }

    @Test
    void shouldGenerateTokenWithCorrectSubject() {
        // Given
        Email email = new Email("user@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then
        String subject = extractSubjectFromToken(token.value());
        assertEquals("user@example.com", subject);
    }

    @Test
    void shouldGenerateTokenWithCorrectExpiration() {
        // Given
        Email email = new Email("test@example.com");
        LocalDateTime beforeGeneration = LocalDateTime.now();

        // When
        JwtToken token = adapter.generateToken(email);

        // Then
        LocalDateTime expectedExpiration = beforeGeneration.plusSeconds(EXPIRATION_MS / 1000);
        
        // Verify expiration in token matches expected (with 2 second tolerance)
        assertTrue(token.expiresAt().isAfter(expectedExpiration.minusSeconds(2)));
        assertTrue(token.expiresAt().isBefore(expectedExpiration.plusSeconds(2)));
        
        // Verify token is not expired
        assertFalse(token.isExpired());
        assertTrue(token.isValid());
    }

    @Test
    void shouldGenerateTokenThatCanBeValidatedByJwtUtil() {
        // Given
        Email email = new Email("validate@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then - Extract username using JwtUtil
        String extractedUsername = jwtUtil.extractUsername(token.value());
        assertEquals("validate@example.com", extractedUsername);
        
        // Verify token is not expired according to JwtUtil
        Date expiration = jwtUtil.extractExpiration(token.value());
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldGenerateTokenWithValidSignature() {
        // Given
        Email email = new Email("signature@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then - Token should be parseable with the secret key
        assertDoesNotThrow(() -> {
            Claims claims = parseToken(token.value());
            assertNotNull(claims);
            assertEquals("signature@example.com", claims.getSubject());
        });
    }

    @Test
    void shouldGenerateTokenWithIssuedAtClaim() {
        // Given
        Email email = new Email("issued@example.com");
        Date beforeGeneration = new Date();

        // When
        JwtToken token = adapter.generateToken(email);

        // Then
        Claims claims = parseToken(token.value());
        Date issuedAt = claims.getIssuedAt();
        
        assertNotNull(issuedAt);
        assertTrue(issuedAt.after(new Date(beforeGeneration.getTime() - 1000))); // 1 second before
        assertTrue(issuedAt.before(new Date(System.currentTimeMillis() + 1000))); // 1 second after
    }

    @Test
    void shouldGenerateTokenWithExpirationClaim() {
        // Given
        Email email = new Email("expiry@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then
        Claims claims = parseToken(token.value());
        Date expiration = claims.getExpiration();
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        
        // Verify expiration is approximately 1 hour from now
        long expectedExpirationTime = System.currentTimeMillis() + EXPIRATION_MS;
        long actualExpirationTime = expiration.getTime();
        long difference = Math.abs(expectedExpirationTime - actualExpirationTime);
        
        // Allow 2 seconds tolerance
        assertTrue(difference < 2000, "Expiration time difference: " + difference + "ms");
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentEmails() {
        // Given
        Email email1 = new Email("user1@example.com");
        Email email2 = new Email("user2@example.com");

        // When
        JwtToken token1 = adapter.generateToken(email1);
        JwtToken token2 = adapter.generateToken(email2);

        // Then
        assertNotEquals(token1.value(), token2.value());
        
        // Verify subjects are different
        String subject1 = extractSubjectFromToken(token1.value());
        String subject2 = extractSubjectFromToken(token2.value());
        assertEquals("user1@example.com", subject1);
        assertEquals("user2@example.com", subject2);
    }

    @Test
    void shouldGenerateDifferentTokensForSameEmailAtDifferentTimes() throws InterruptedException {
        // Given
        Email email = new Email("same@example.com");

        // When
        JwtToken token1 = adapter.generateToken(email);
        Thread.sleep(1000); // Wait 1 second to ensure different issuedAt
        JwtToken token2 = adapter.generateToken(email);

        // Then
        assertNotEquals(token1.value(), token2.value());
        
        // Verify both have same subject but different issuedAt times
        Claims claims1 = parseToken(token1.value());
        Claims claims2 = parseToken(token2.value());
        
        assertEquals(claims1.getSubject(), claims2.getSubject());
        assertTrue(claims2.getIssuedAt().after(claims1.getIssuedAt()));
    }

    @Test
    void shouldGenerateTokenThatMatchesJwtTokenDomainModel() {
        // Given
        Email email = new Email("domain@example.com");

        // When
        JwtToken token = adapter.generateToken(email);

        // Then - Verify JwtToken domain object properties
        assertNotNull(token.value());
        assertFalse(token.value().isBlank());
        assertNotNull(token.expiresAt());
        
        // Verify domain methods work correctly
        assertTrue(token.isValid());
        assertFalse(token.isExpired());
        
        // Verify expiration matches token claims
        Claims claims = parseToken(token.value());
        LocalDateTime claimExpiration = LocalDateTime.ofInstant(
            claims.getExpiration().toInstant(),
            ZoneId.systemDefault()
        );
        
        // Allow 1 second tolerance
        assertTrue(Math.abs(
            token.expiresAt().atZone(ZoneId.systemDefault()).toEpochSecond() -
            claimExpiration.atZone(ZoneId.systemDefault()).toEpochSecond()
        ) <= 1);
    }

    // Helper methods

    private String extractSubjectFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    private Claims parseToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
