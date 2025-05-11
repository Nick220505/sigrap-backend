package com.sigrap.user;

import com.sigrap.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user in the system.
 * Users are the authenticated entities that can access the application.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /**
   * Unique identifier for the user.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Full name of the user.
   * Must not be blank.
   */
  @NotBlank
  private String name;

  /**
   * Email address of the user.
   * Must be a valid email format and unique in the system.
   */
  @NotBlank
  @Email
  @Column(unique = true)
  private String email;

  /**
   * Encrypted password of the user.
   * Must not be blank and should be stored in encrypted form.
   */
  @NotBlank
  private String password;

  /**
   * Phone number of the user.
   */
  private String phone;

  /**
   * Current status of the user's account.
   */
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  /**
   * Timestamp of the user's last successful login.
   */
  private LocalDateTime lastLogin;

  /**
   * Number of consecutive failed login attempts.
   * Reset to 0 on successful login.
   */
  @Builder.Default
  private Integer failedAttempts = 0;

  /**
   * Token for password reset operations.
   * Null when no reset is in progress.
   */
  private String passwordResetToken;

  /**
   * Expiration timestamp for the password reset token.
   */
  private LocalDateTime passwordResetExpiry;

  /**
   * Collection of roles assigned to this user.
   * Many-to-many relationship with Role.
   */
  @ManyToMany
  @JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  /**
   * Collection of notification preferences for this user.
   * One-to-many relationship with UserNotificationPreference.
   * Excluded from equals/hashCode to prevent LazyInitializationException.
   */
  @OneToMany(mappedBy = "user")
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<UserNotificationPreference> notificationPreferences =
    new HashSet<>();

  /**
   * Enum representing possible user account statuses.
   */
  public enum UserStatus {
    /**
     * User account is active and can be used for login.
     */
    ACTIVE,

    /**
     * User account is locked due to security concerns or policy violations.
     */
    LOCKED,

    /**
     * User account is inactive and cannot be used for login.
     */
    INACTIVE,
  }
}
