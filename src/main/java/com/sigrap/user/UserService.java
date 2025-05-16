package com.sigrap.user;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for user management operations and implementing Spring Security's UserDetailsService.
 * Handles user authentication and user management operations.
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
  private final UserMapper userMapper;

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

    return org.springframework.security.core.userdetails.User.builder()
      .username(user.getEmail())
      .password(user.getPassword())
      .authorities(new ArrayList<>()) // Simplified, actual authorities depend on UserRole
      .disabled(!user.isEnabled()) // isEnabled now always true
      .accountLocked(!user.isAccountNonLocked()) // isAccountNonLocked now always true
      .build();
  }

  /**
   * Retrieves all users.
   *
   * @return List of UserInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<UserInfo> findAll() {
    List<com.sigrap.user.User> users = userRepository.findAll();
    return userMapper.toInfoList(users);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id The ID of the user to retrieve
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserInfo findById(Long id) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    return userMapper.toInfo(user);
  }

  /**
   * Retrieves a user by their email address.
   *
   * @param email The email address of the user to retrieve
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserInfo findByEmail(String email) {
    com.sigrap.user.User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with email: " + email)
      );
    return userMapper.toInfo(user);
  }

  /**
   * Creates a new user.
   *
   * @param userData The data for the new user
   * @return UserInfo containing the created user's information
   * @throws IllegalArgumentException if a user already exists with the email
   */
  @Transactional
  public UserInfo create(UserData userData) {
    if (userRepository.existsByEmail(userData.getEmail())) {
      throw new IllegalArgumentException(
        "Email already in use: " + userData.getEmail()
      );
    }
    if (
      userData.getDocumentId() != null &&
      userRepository.existsByDocumentId(userData.getDocumentId())
    ) {
      throw new IllegalArgumentException(
        "Document ID already in use: " + userData.getDocumentId()
      );
    }

    com.sigrap.user.User user = userMapper.toEntity(userData);
    com.sigrap.user.User savedUser = userRepository.save(user);
    return userMapper.toInfo(savedUser);
  }

  /**
   * Updates an existing user.
   *
   * @param id The ID of the user to update
   * @param userData The new data for the user
   * @return UserInfo containing the updated user's information
   * @throws EntityNotFoundException if the user is not found
   * @throws IllegalArgumentException if the email is already in use by another user
   */
  @Transactional
  public UserInfo update(Long id, UserData userData) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (
      userData.getEmail() != null &&
      !userData.getEmail().equals(user.getEmail()) &&
      userRepository.existsByEmail(userData.getEmail())
    ) {
      throw new IllegalArgumentException(
        "Email already in use: " + userData.getEmail()
      );
    }
    if (
      userData.getDocumentId() != null &&
      !userData.getDocumentId().equals(user.getDocumentId()) &&
      userRepository.existsByDocumentId(userData.getDocumentId())
    ) {
      throw new IllegalArgumentException(
        "Document ID already in use: " + userData.getDocumentId()
      );
    }

    userMapper.updateEntityFromData(user, userData);

    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Deletes a user.
   *
   * @param id The ID of the user to delete
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public void delete(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found: " + id);
    }
    userRepository.deleteById(id);
  }

  /**
   * Deletes multiple users by their IDs.
   *
   * @param ids The list of user IDs to delete
   * @throws EntityNotFoundException if any user is not found
   */
  @Transactional
  public void deleteAllById(List<Long> ids) {
    for (Long id : ids) {
      if (!userRepository.existsById(id)) {
        throw new EntityNotFoundException("User not found: " + id);
      }
    }
    userRepository.deleteAllById(ids);
  }

  /**
   * Updates a user's profile information.
   *
   * @param id The ID of the user to update
   * @param userData The new profile data
   * @return UserInfo containing the updated profile information
   */
  @Transactional
  public UserInfo updateProfile(Long id, UserData userData) {
    return update(id, userData);
  }

  /**
   * Changes a user's password.
   *
   * @param id The ID of the user
   * @param currentPassword The current password for verification
   * @param newPassword The new password to set
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if the user is not found
   * @throws IllegalArgumentException if the current password is incorrect
   */
  @Transactional
  public UserInfo changePassword(
    Long id,
    String currentPassword, // currentPassword might not be verifiable if not stored or if admin reset
    String newPassword
  ) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    UserData passwordUpdate = new UserData();
    passwordUpdate.setPassword(newPassword);
    userMapper.updateEntityFromData(user, passwordUpdate);

    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Records a successful login.
   *
   * @param email The email address of the user
   */
  @Transactional
  public void registerSuccessfulLogin(String email) {
    com.sigrap.user.User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with email: " + email)
      );
    user.setLastLogin(
      ZonedDateTime.now(ZoneId.of("America/Bogota")).toLocalDateTime()
    );
    userRepository.save(user);
  }
}
