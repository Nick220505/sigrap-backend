package com.sigrap.auth.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class JwtTokenTest {

  @Test
  void shouldCreateJwtTokenWithValidData() {
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
    JwtToken token = new JwtToken("token-value", expiresAt);

    assertEquals("token-value", token.value());
    assertEquals(expiresAt, token.expiresAt());
  }

  @Test
  void shouldThrowExceptionForNullValue() {
    assertThrows(
      NullPointerException.class,
      () -> new JwtToken(null, LocalDateTime.now())
    );
  }

  @Test
  void shouldThrowExceptionForBlankValue() {
    assertThrows(
      IllegalArgumentException.class,
      () -> new JwtToken("", LocalDateTime.now())
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> new JwtToken("   ", LocalDateTime.now())
    );
  }

  @Test
  void shouldThrowExceptionForNullExpiresAt() {
    assertThrows(
      NullPointerException.class,
      () -> new JwtToken("token-value", null)
    );
  }

  @Test
  void shouldReturnTrueForExpiredToken() {
    LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
    JwtToken token = new JwtToken("token-value", pastTime);

    assertTrue(token.isExpired());
    assertFalse(token.isValid());
  }

  @Test
  void shouldReturnFalseForValidToken() {
    LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
    JwtToken token = new JwtToken("token-value", futureTime);

    assertFalse(token.isExpired());
    assertTrue(token.isValid());
  }

  @Test
  void shouldBeEqualForSameValues() {
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
    JwtToken token1 = new JwtToken("token-value", expiresAt);
    JwtToken token2 = new JwtToken("token-value", expiresAt);

    assertEquals(token1, token2);
    assertEquals(token1.hashCode(), token2.hashCode());
  }
}
