package com.sigrap.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
}
