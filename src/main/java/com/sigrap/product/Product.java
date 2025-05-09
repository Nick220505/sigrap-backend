package com.sigrap.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sigrap.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a product in the system.
 * Products are the main items that can be sold and managed in the inventory.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  /**
   * Unique identifier for the product.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Name of the product.
   * Must not be blank and is required.
   */
  @NotBlank(message = "Product name cannot be blank")
  @Column(nullable = false)
  private String name;

  /**
   * Optional description of the product providing additional details.
   */
  private String description;

  /**
   * Cost price of the product.
   * Must be zero or positive and is required.
   */
  @NotNull(message = "Cost price cannot be null")
  @PositiveOrZero(message = "Cost price must be zero or positive")
  @Column(name = "cost_price", nullable = false)
  private BigDecimal costPrice;

  /**
   * Sale price of the product.
   * Must be zero or positive and is required.
   */
  @NotNull(message = "Sale price cannot be null")
  @PositiveOrZero(message = "Sale price must be zero or positive")
  @Column(name = "sale_price", nullable = false)
  private BigDecimal salePrice;

  /**
   * Category to which this product belongs.
   * Optional many-to-one relationship with Category.
   */
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  /**
   * Timestamp of when the product was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the product was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
