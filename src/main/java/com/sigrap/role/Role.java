package com.sigrap.role;

import com.sigrap.permission.Permission;
import com.sigrap.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
 * Entity class representing a role in the system.
 * Roles are collections of permissions that can be assigned to users.
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

  /**
   * Unique identifier for the role.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the role.
   * Must be unique and not blank.
   */
  @NotBlank
  @Column(unique = true)
  private String name;

  /**
   * Description of the role's purpose and scope.
   */
  @Column(length = 500)
  private String description;

  /**
   * Collection of permissions assigned to this role.
   * Many-to-many relationship with Permission.
   * Excluded from equals/hashCode to prevent LazyInitializationException.
   */
  @ManyToMany
  @JoinTable(
    name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<Permission> permissions = new HashSet<>();

  /**
   * Collection of users assigned to this role.
   * Many-to-many relationship with User.
   * Excluded from equals/hashCode to prevent LazyInitializationException.
   */
  @ManyToMany(mappedBy = "roles")
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<User> users = new HashSet<>();

  /**
   * Timestamp when the role was created.
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp when the role was last updated.
   */
  private LocalDateTime updatedAt;
}
