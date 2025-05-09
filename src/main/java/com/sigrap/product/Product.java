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

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Product name cannot be blank")
  @Column(nullable = false)
  private String name;

  private String description;

  @NotNull(message = "Cost price cannot be null")
  @PositiveOrZero(message = "Cost price must be zero or positive")
  @Column(name = "cost_price", nullable = false)
  private BigDecimal costPrice;

  @NotNull(message = "Sale price cannot be null")
  @PositiveOrZero(message = "Sale price must be zero or positive")
  @Column(name = "sale_price", nullable = false)
  private BigDecimal salePrice;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
