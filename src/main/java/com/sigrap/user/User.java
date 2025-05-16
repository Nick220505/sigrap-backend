package com.sigrap.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {

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
   * The role of the user in the system.
   */
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private UserRole role = UserRole.EMPLOYEE;

  /**
   * Timestamp of the user's last successful login.
   */
  @Column(name = "last_login")
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

  @Override
  public Set<GrantedAuthority> getAuthorities() {
    return Set.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return status != UserStatus.LOCKED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }
}
