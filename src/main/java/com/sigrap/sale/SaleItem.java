package com.sigrap.sale;

import com.sigrap.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an item within a sale.
 *
 * <p>Each sale item corresponds to a specific product being sold as part of a sale,
 * including the quantity, price per unit, and any discounts applied to this specific item.</p>
 */
@Entity
@Table(name = "sale_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {

  /**
   * Unique identifier for the sale item.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The sale this item belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sale_id", nullable = false)
  private Sale sale;

  /**
   * The product being sold.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  /**
   * The quantity of the product being sold.
   */
  @NotNull(message = "Quantity cannot be null")
  @Positive(message = "Quantity must be positive")
  @Column(nullable = false)
  private Integer quantity;

  /**
   * The unit price of the product at the time of sale.
   */
  @NotNull(message = "Unit price cannot be null")
  @PositiveOrZero(message = "Unit price must be zero or positive")
  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  /**
   * The subtotal for this item (quantity * unit price).
   */
  @NotNull(message = "Subtotal cannot be null")
  @PositiveOrZero(message = "Subtotal must be zero or positive")
  @Column(nullable = false)
  private BigDecimal subtotal;

  /**
   * Calculate and set the subtotal based on quantity and unit price.
   */
  @PrePersist
  @PreUpdate
  public void calculateSubtotal() {
    if (quantity != null && unitPrice != null) {
      BigDecimal totalBeforeDiscount = unitPrice.multiply(
        BigDecimal.valueOf(quantity)
      );
      subtotal = totalBeforeDiscount;
      if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
        subtotal = BigDecimal.ZERO;
      }
    }
  }
}
