package com.sigrap.user;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class implementing Spring Security's UserDetailsService.
 * Handles user authentication and user details retrieval for security purposes.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  /**
   * Repository for database operations on users.
   * Provides functionality for finding users by email and other user-related
   * queries.
   */
  private final UserRepository userRepository;

  /**
   * Loads a user's details by their email address for authentication.
   * This method is required by UserDetailsService and is used by Spring Security
   * during the authentication process.
   *
   * @param email The email address of the user to load (used as username)
   * @return UserDetails object containing the user's security information
   * @throws UsernameNotFoundException if no user is found with the given email
   */
  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    com.sigrap.user.User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException("User not found with email: " + email)
      );

    return User.builder()
      .username(user.getEmail())
      .password(user.getPassword())
      .authorities(new ArrayList<>())
      .build();
  }
}
