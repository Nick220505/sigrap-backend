package com.sigrap.permission;

import com.sigrap.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Entity class representing a permission in the system.
 * Permissions define specific actions that can be performed on resources.
 */
@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

  /**
   * Unique identifier for the permission.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the permission.
   * Must be unique and not blank.
   */
  @NotBlank
  @Column(unique = true)
  private String name;

  /**
   * Description of what this permission allows.
   */
  @Column(length = 500)
  private String description;

  /**
   * The resource that this permission applies to.
   * Examples: "users", "products", "orders".
   */
  @NotBlank
  private String resource;

  /**
   * The action that this permission allows on the resource.
   * Examples: "read", "write", "delete", "update".
   */
  @NotBlank
  private String action;

  /**
   * Collection of roles that include this permission.
   * Many-to-many relationship with Role.
   * Excluded from equals/hashCode to prevent LazyInitializationException.
   */
  @ManyToMany(mappedBy = "permissions")
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<Role> roles = new HashSet<>();

  /**
   * Timestamp when the permission was created.
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp when the permission was last updated.
   */
  private LocalDateTime updatedAt;
}
