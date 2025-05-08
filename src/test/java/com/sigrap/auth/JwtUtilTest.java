package com.sigrap.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

class JwtUtilTest {

  @InjectMocks
  private JwtUtil jwtUtil;

  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKey123456789012345678901234567890");
    ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

    userDetails = User.builder()
        .username("test@example.com")
        .password("password")
        .authorities(Collections.emptyList())
        .build();
  }

  @Test
  void generateToken_shouldCreateValidToken() {
    String token = jwtUtil.generateToken(userDetails);

    assertThat(token).isNotNull();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void extractUsername_shouldExtractCorrectUsername() {
    String token = jwtUtil.generateToken(userDetails);

    String username = jwtUtil.extractUsername(token);

    assertThat(username).isEqualTo("test@example.com");
  }

  @Test
  void extractExpiration_shouldExtractCorrectExpirationDate() {
    String token = jwtUtil.generateToken(userDetails);

    Date expiration = jwtUtil.extractExpiration(token);

    Date now = new Date();
    assertThat(expiration).isAfter(now);
    assertThat(expiration.getTime() - now.getTime())
        .isGreaterThan(3600000L - 5000L)
        .isLessThan(3600000L + 5000L);
  }

  @Test
  void extractClaim_shouldExtractCustomClaim() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("testKey", "testValue");

    String token = (String) ReflectionTestUtils.invokeMethod(jwtUtil, "createToken", claims, userDetails.getUsername());

    String customClaim = jwtUtil.extractClaim(token, claimsObj -> claimsObj.get("testKey", String.class));

    assertThat(customClaim).isEqualTo("testValue");
  }

  @Test
  void validateToken_shouldReturnTrue_forValidToken() {
    String token = jwtUtil.generateToken(userDetails);

    boolean isValid = jwtUtil.validateToken(token, userDetails);

    assertThat(isValid).isTrue();
  }

  @Test
  void validateToken_shouldReturnFalse_forDifferentUser() {
    String token = jwtUtil.generateToken(userDetails);

    UserDetails differentUser = User.builder()
        .username("other@example.com")
        .password("password")
        .authorities(Collections.emptyList())
        .build();
    boolean isValid = jwtUtil.validateToken(token, differentUser);

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_shouldReturnFalse_forExpiredToken() {
    String token = jwtUtil.generateToken(userDetails);

    JwtUtil expiredTokenJwtUtil = new JwtUtil() {
      @Override
      public Date extractExpiration(String t) {
        return new Date(System.currentTimeMillis() - 10000L);
      }

      @Override
      public String extractUsername(String t) {
        return userDetails.getUsername();
      }
    };

    ReflectionTestUtils.setField(expiredTokenJwtUtil, "secret", "testSecretKey123456789012345678901234567890");

    boolean isValid = expiredTokenJwtUtil.validateToken(token, userDetails);

    assertThat(isValid).isFalse();
  }

  @Test
  void extractAllClaims_shouldThrowException_forExpiredToken() {
    ReflectionTestUtils.setField(jwtUtil, "expiration", -10000L);
    String expiredToken = jwtUtil.generateToken(userDetails);

    ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

    assertThrows(ExpiredJwtException.class,
        () -> ReflectionTestUtils.invokeMethod(jwtUtil, "extractAllClaims", expiredToken));
  }

  @Test
  void isTokenExpired_shouldReturnTrue_forExpiredToken() {
    Date pastDate = new Date(System.currentTimeMillis() - 10000L);

    JwtUtil spyJwtUtil = new JwtUtil() {
      @Override
      public Date extractExpiration(String token) {
        return pastDate;
      }
    };

    ReflectionTestUtils.setField(spyJwtUtil, "secret", "testSecretKey123456789012345678901234567890");

    Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(spyJwtUtil, "isTokenExpired", "dummy-token");

    assertThat(result).isTrue();
  }

  @Test
  void isTokenExpired_shouldReturnFalse_forValidToken() {
    Date futureDate = new Date(System.currentTimeMillis() + 3600000L);

    JwtUtil spyJwtUtil = new JwtUtil() {
      @Override
      public Date extractExpiration(String token) {
        return futureDate;
      }
    };

    ReflectionTestUtils.setField(spyJwtUtil, "secret", "testSecretKey123456789012345678901234567890");

    Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(spyJwtUtil, "isTokenExpired", "dummy-token");

    assertThat(result).isFalse();
  }

  @Test
  void extractAllClaims_shouldExtractAllClaims() {
    String token = jwtUtil.generateToken(userDetails);

    Claims claims = (Claims) ReflectionTestUtils.invokeMethod(jwtUtil, "extractAllClaims", token);

    assertThat(claims).isNotNull();
    if (claims != null) {
      assertThat(claims.getSubject()).isEqualTo("test@example.com");
      assertThat(claims.getExpiration()).isAfter(new Date());
    }
  }

  @Test
  void getSigningKey_shouldCreateValidSigningKey() {
    Object signingKey = ReflectionTestUtils.invokeMethod(jwtUtil, "getSigningKey");

    assertThat(signingKey).isNotNull();
  }
}