package com.sigrap.auth.infrastructure.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and claim extraction.
 * 
 * <p>This utility is used by JWT adapters in the hexagonal architecture.
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  /**
   * Generates a JWT token for the given user details.
   *
   * @param userDetails the user details to generate the token for
   * @return the generated JWT token
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Creates a JWT token with the given claims and subject.
   *
   * @param claims the claims to include in the token
   * @param subject the subject (username) of the token
   * @return the generated JWT token
   */
  private String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
      .claims(claims)
      .subject(subject)
      .issuedAt(now)
      .expiration(expirationDate)
      .signWith(getSigningKey())
      .compact();
  }

  /**
   * Extracts the username from the JWT token.
   *
   * @param token the JWT token
   * @return the username extracted from the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from the JWT token.
   *
   * @param token the JWT token
   * @return the expiration date extracted from the token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts a specific claim from the JWT token.
   *
   * @param token the JWT token
   * @param claimsResolver the function to extract the claim
   * @param <T> the type of the claim
   * @return the extracted claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from the JWT token.
   *
   * @param token the JWT token
   * @return all claims from the token
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  /**
   * Validates the JWT token against the user details.
   *
   * @param token the JWT token to validate
   * @param userDetails the user details to validate against
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Checks if the JWT token is expired.
   *
   * @param token the JWT token
   * @return true if the token is expired, false otherwise
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Gets the signing key for JWT operations.
   *
   * @return the signing key
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
