package com.sigrap.auth.infrastructure.adapter.out.persistence;

import com.sigrap.auth.domain.model.Credentials;
import com.sigrap.auth.domain.model.Email;
import com.sigrap.auth.domain.model.RegistrationData;
import com.sigrap.auth.domain.port.PasswordEncoderPort;
import com.sigrap.auth.domain.port.UserAuthenticationPort;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.RoleJpaRepository;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sigrap.user.infrastructure.adapter.out.persistence.UserJpaRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Persistence adapter implementation for UserAuthenticationPort.
 * This adapter bridges the auth module to the user module's persistence layer.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Implements the UserAuthenticationPort interface from the domain layer</li>
 *   <li>Delegates to the User module's JPA repository for data access</li>
 *   <li>Handles authentication logic by validating credentials</li>
 *   <li>Manages user registration by creating new user entities</li>
 * </ul>
 * 
 * <p>This is an output adapter in hexagonal architecture terminology.
 */
@Component
public class UserAuthenticationAdapter implements UserAuthenticationPort {

  private final UserJpaRepository userJpaRepository;
  private final RoleJpaRepository roleJpaRepository;
  private final PasswordEncoderPort passwordEncoder;

  /**
   * Constructor for dependency injection.
   *
   * @param userJpaRepository the User module's JPA repository
   * @param roleJpaRepository the Role JPA repository for assigning default roles
   * @param passwordEncoder the password encoder for validating credentials
   */
  public UserAuthenticationAdapter(
    UserJpaRepository userJpaRepository,
    RoleJpaRepository roleJpaRepository,
    PasswordEncoderPort passwordEncoder
  ) {
    this.userJpaRepository = userJpaRepository;
    this.roleJpaRepository = roleJpaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Authenticates a user with the given credentials.
   *
   * @param credentials the user credentials
   * @throws IllegalArgumentException if authentication fails
   */
  @Override
  public void authenticate(Credentials credentials) {
    UserJpaEntity user = userJpaRepository
      .findByEmail(credentials.email().value())
      .orElseThrow(() ->
        new IllegalArgumentException("Invalid email or password")
      );

    if (!user.isEnabled()) {
      throw new IllegalArgumentException("User account is disabled");
    }

    if (!passwordEncoder.matches(credentials.password(), user.getHashedPassword())) {
      throw new IllegalArgumentException("Invalid email or password");
    }
  }

  /**
   * Finds user information by email.
   *
   * @param email the email to search for
   * @return optional containing user info if found
   */
  @Override
  public Optional<UserInfo> findUserByEmail(Email email) {
    return userJpaRepository
      .findByEmail(email.value())
      .map(this::toUserInfo);
  }

  /**
   * Checks if a user with the given email exists.
   *
   * @param email the email to check
   * @return true if the user exists, false otherwise
   */
  @Override
  public boolean existsByEmail(Email email) {
    return userJpaRepository.existsByEmail(email.value());
  }

  /**
   * Registers a new user.
   *
   * @param registrationData the registration data
   * @param encodedPassword the encoded password
   * @return the created user info
   */
  @Override
  public UserInfo registerUser(
    RegistrationData registrationData,
    String encodedPassword
  ) {
    // Get default USER role
    RoleJpaEntity userRole = roleJpaRepository
      .findByName("USER")
      .orElseThrow(() ->
        new IllegalStateException("Default USER role not found in database")
      );

    // Create user entity
    Set<RoleJpaEntity> roles = new HashSet<>();
    roles.add(userRole);

    UserJpaEntity user = UserJpaEntity.builder()
      .username(registrationData.name())
      .email(registrationData.email().value())
      .hashedPassword(encodedPassword)
      .enabled(true)
      .roles(roles)
      .build();

    // Save user
    UserJpaEntity savedUser = userJpaRepository.save(user);

    return toUserInfo(savedUser);
  }

  /**
   * Updates the last login timestamp for a user.
   *
   * @param email the email of the user
   */
  @Override
  public void updateLastLogin(Email email) {
    // Note: The current UserJpaEntity doesn't have a lastLogin field
    // This is a placeholder implementation
    // If needed, the User module's entity should be extended with a lastLogin field
    
    // For now, we'll just verify the user exists
    userJpaRepository
      .findByEmail(email.value())
      .ifPresent(user -> {
        // In a real implementation, we would update the lastLogin timestamp
        // user.setLastLogin(LocalDateTime.now());
        // userJpaRepository.save(user);
      });
  }

  /**
   * Converts a UserJpaEntity to UserInfo.
   *
   * @param user the JPA entity
   * @return the UserInfo value object
   */
  private UserInfo toUserInfo(UserJpaEntity user) {
    Email email = new Email(user.getEmail());
    
    // Get the first role name (simplified - in reality might have multiple roles)
    String roleName = user.getRoles().stream()
      .findFirst()
      .map(RoleJpaEntity::getName)
      .orElse("USER");

    return new UserInfo(
      email,
      user.getUsername(),
      user.getHashedPassword(),
      LocalDateTime.now(), // Using current time as placeholder for lastLogin
      roleName
    );
  }
}
