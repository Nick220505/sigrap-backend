package com.sigrap.user;

import com.sigrap.role.Role;
import com.sigrap.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
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
  private final RoleRepository roleRepository;
  private static final long PASSWORD_RESET_EXPIRY_MINUTES = 30;
  private static final int MAX_FAILED_ATTEMPTS = 5;

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

    userMapper.updateEntityFromData(user, userData);

    if (userData.getRoleIds() != null) {
      userMapper.updateRoles(user, userData.getRoleIds());
    }

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
    // Verify all users exist before deleting any
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
    String currentPassword,
    String newPassword
  ) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    UserData passwordUpdate = new UserData();
    passwordUpdate.setPassword(newPassword);
    userMapper.updateEntityFromData(user, passwordUpdate);

    user.setFailedAttempts(0);
    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Initiates a password reset for a user.
   *
   * @param email The email address of the user
   * @return The generated reset token
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public String initiatePasswordReset(String email) {
    com.sigrap.user.User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found with email: " + email)
      );

    String resetToken = UUID.randomUUID().toString();
    user.setPasswordResetToken(resetToken);
    user.setPasswordResetExpiry(
      LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES)
    );

    userRepository.save(user);
    return resetToken;
  }

  /**
   * Completes a password reset using a valid token.
   *
   * @param token The password reset token
   * @param newPassword The new password to set
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if no user is found with the token
   * @throws IllegalArgumentException if the token is expired
   */
  @Transactional
  public UserInfo resetPassword(String token, String newPassword) {
    com.sigrap.user.User user = userRepository
      .findByPasswordResetToken(token)
      .orElseThrow(() ->
        new EntityNotFoundException("Invalid password reset token")
      );

    if (
      user.getPasswordResetExpiry() == null ||
      user.getPasswordResetExpiry().isBefore(LocalDateTime.now())
    ) {
      throw new IllegalArgumentException("Password reset token has expired");
    }

    UserData passwordUpdate = new UserData();
    passwordUpdate.setPassword(newPassword);
    userMapper.updateEntityFromData(user, passwordUpdate);

    user.setPasswordResetToken(null);
    user.setPasswordResetExpiry(null);
    user.setFailedAttempts(0);
    if (user.getStatus() == com.sigrap.user.User.UserStatus.LOCKED) {
      user.setStatus(com.sigrap.user.User.UserStatus.ACTIVE);
    }

    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Locks a user's account.
   *
   * @param id The ID of the user to lock
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public UserInfo lockAccount(Long id) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    user.setStatus(com.sigrap.user.User.UserStatus.LOCKED);
    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Unlocks a user's account.
   *
   * @param id The ID of the user to unlock
   * @return UserInfo containing the user's information
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public UserInfo unlockAccount(Long id) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    user.setStatus(com.sigrap.user.User.UserStatus.ACTIVE);
    user.setFailedAttempts(0);
    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Records a failed login attempt and locks the account if the maximum number
   * of attempts is reached.
   *
   * @param email The email address of the user
   * @return true if the account is now locked, false otherwise
   */
  @Transactional
  public boolean registerFailedLogin(String email) {
    com.sigrap.user.User user = userRepository.findByEmail(email).orElse(null);

    if (
      user != null && user.getStatus() == com.sigrap.user.User.UserStatus.ACTIVE
    ) {
      user.setFailedAttempts(user.getFailedAttempts() + 1);

      if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
        user.setStatus(com.sigrap.user.User.UserStatus.LOCKED);
        userRepository.save(user);
        return true;
      }

      userRepository.save(user);
    }
    return false;
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
      .orElseThrow(() -> new EntityNotFoundException("User not found"));

    user.setLastLogin(
      ZonedDateTime.now(ZoneId.of("America/Bogota")).toLocalDateTime()
    );
    userRepository.save(user);
  }

  /**
   * Gets the roles assigned to a user.
   *
   * @param id The ID of the user
   * @return Set of roles assigned to the user
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public Set<Role> getRoles(Long id) {
    com.sigrap.user.User user = userRepository
      .findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    return user.getRoles();
  }

  /**
   * Assigns a role to a user.
   *
   * @param userId The ID of the user
   * @param roleId The ID of the role to assign
   * @return UserInfo containing the updated user information
   * @throws EntityNotFoundException if the user or role is not found
   */
  @Transactional
  public UserInfo assignRole(Long userId, Long roleId) {
    com.sigrap.user.User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    user.getRoles().add(role);
    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }

  /**
   * Removes a role from a user.
   *
   * @param userId The ID of the user
   * @param roleId The ID of the role to remove
   * @return UserInfo containing the updated user information
   * @throws EntityNotFoundException if the user or role is not found
   */
  @Transactional
  public UserInfo removeRole(Long userId, Long roleId) {
    com.sigrap.user.User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    Role role = roleRepository
      .findById(roleId)
      .orElseThrow(() ->
        new EntityNotFoundException("Role not found: " + roleId)
      );

    user.getRoles().remove(role);
    com.sigrap.user.User updatedUser = userRepository.save(user);
    return userMapper.toInfo(updatedUser);
  }
}
