package com.sigrap.auth.domain.port;

import com.sigrap.auth.domain.model.Credentials;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.RegistrationData;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Port interface for user authentication operations.
 * Defines the contract for interacting with user data for authentication purposes.
 */
public interface UserAuthenticationPort {
  /**
   * Authenticates a user with the given credentials.
   *
   * @param credentials the user credentials
   * @throws IllegalArgumentException if authentication fails
   */
  void authenticate(Credentials credentials);

  /**
   * Finds user information by email.
   *
   * @param email the email to search for
   * @return optional containing user info if found
   */
  Optional<UserInfo> findUserByEmail(Email email);

  /**
   * Checks if a user with the given email exists.
   *
   * @param email the email to check
   * @return true if the user exists, false otherwise
   */
  boolean existsByEmail(Email email);

  /**
   * Registers a new user.
   *
   * @param registrationData the registration data
   * @param encodedPassword the encoded password
   * @return the created user info
   */
  UserInfo registerUser(RegistrationData registrationData, String encodedPassword);

  /**
   * Updates the last login timestamp for a user.
   *
   * @param email the email of the user
   */
  void updateLastLogin(Email email);

  /**
   * Value object representing user information needed for authentication.
   */
  record UserInfo(
    Email email,
    String name,
    String encodedPassword,
    LocalDateTime lastLogin,
    String role
  ) {}
}
