package com.sigrap.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token operations.
 * Handles the creation, validation, and parsing of JWT tokens for authentication.
 */
@Component
public class JwtUtil {

  /**
   * Secret key used for JWT token signing and verification.
   * This value is injected from application properties for security.
   */
  @Value("${jwt.secret}")
  private String secret;

  /**
   * Token expiration time in milliseconds.
   * This value is injected from application properties and determines
   * how long a JWT token remains valid after issuance.
   */
  @Value("${jwt.expiration}")
  private Long expiration;

  /**
   * Extracts the username from a JWT token.
   *
   * @param token JWT token to analyze
   * @return the username stored in the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token JWT token to analyze
   * @return the expiration date of the token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts a specific claim from a JWT token using a claims resolver function.
   *
   * @param token JWT token to analyze
   * @param claimsResolver function to extract the desired claim
   * @return the extracted claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from a JWT token.
   *
   * @param token JWT token to analyze
   * @return all claims stored in the token
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  /**
   * Creates a signing key from the secret.
   *
   * @return SecretKey for JWT operations
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Checks if a JWT token has expired.
   *
   * @param token JWT token to check
   * @return true if the token has expired, false otherwise
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Generates a JWT token for a user.
   *
   * @param userDetails the user details to include in the token
   * @return generated JWT token
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Creates a JWT token with specified claims and subject.
   *
   * @param claims additional claims to include in the token
   * @param subject the subject (typically username) of the token
   * @return generated JWT token
   */
  private String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
      .claims(claims)
      .subject(subject)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(getSigningKey())
      .compact();
  }

  /**
   * Validates a JWT token for a specific user.
   *
   * @param token JWT token to validate
   * @param userDetails the user details to validate against
   * @return true if the token is valid for the user, false otherwise
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (
      username.equals(userDetails.getUsername()) && !isTokenExpired(token)
    );
  }
}
