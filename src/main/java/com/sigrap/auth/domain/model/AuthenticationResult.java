package com.sigrap.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing the result of a successful authentication.
 * Contains the JWT token and user information returned after authentication.
 */
public class AuthenticationResult {

  private final JwtToken token;
  private final Email email;
  private final String name;
  private final LocalDateTime lastLogin;
  private final String role;

  /**
   * Creates a new authentication result.
   *
   * @param token the JWT token generated for the authenticated user
   * @param email the email of the authenticated user
   * @param name the name of the authenticated user
   * @param lastLogin the timestamp of the last login
   * @param role the role of the authenticated user
   * @throws NullPointerException if any required parameter is null
   */
  public AuthenticationResult(
    JwtToken token,
    Email email,
    String name,
    LocalDateTime lastLogin,
    String role
  ) {
    this.token = Objects.requireNonNull(token, "Token cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.lastLogin = Objects.requireNonNull(lastLogin, "Last login cannot be null");
    this.role = Objects.requireNonNull(role, "Role cannot be null");
  }

  public JwtToken getToken() {
    return token;
  }

  public Email getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getLastLogin() {
    return lastLogin;
  }

  public String getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthenticationResult that = (AuthenticationResult) o;
    return (
      Objects.equals(token, that.token) &&
      Objects.equals(email, that.email) &&
      Objects.equals(name, that.name) &&
      Objects.equals(lastLogin, that.lastLogin) &&
      Objects.equals(role, that.role)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, email, name, lastLogin, role);
  }

  @Override
  public String toString() {
    return (
      "AuthenticationResult{" +
      "email=" +
      email +
      ", name='" +
      name +
      '\'' +
      ", lastLogin=" +
      lastLogin +
      ", role='" +
      role +
      '\'' +
      '}'
    );
  }
}
