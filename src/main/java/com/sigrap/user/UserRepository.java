package com.sigrap.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity operations.
 * Provides methods for user-specific database operations beyond basic CRUD.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Finds a user by their email address.
   *
   * @param email The email address to search for
   * @return Optional containing the user if found, empty otherwise
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a user exists with the given email address.
   *
   * @param email The email address to check
   * @return true if a user exists with the email, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Finds a user by their password reset token.
   *
   * @param token The password reset token to search for
   * @return Optional containing the user if found, empty otherwise
   */
  Optional<User> findByPasswordResetToken(String token);
}
