package com.sigrap.category;

import com.sigrap.product.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing a product category in the system.
 * Categories are used to organize and classify products.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  /**
   * Unique identifier for the category.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Name of the category.
   * Must be unique and cannot be blank.
   */
  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, unique = true)
  private String name;

  /**
   * Optional description of the category.
   */
  @Size(max = 500)
  private String description;

  /**
   * Timestamp of when the category was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the category was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(
    mappedBy = "category",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Product> products;
}
