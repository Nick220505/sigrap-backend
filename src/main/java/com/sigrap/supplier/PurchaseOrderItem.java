package com.sigrap.supplier;

import com.sigrap.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing an item in a purchase order.
 * Purchase order items track individual products ordered from suppliers.
 */
@Entity
@Table(name = "purchase_order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

  /**
   * Enum defining possible item statuses.
   */
  public enum Status {
    PENDING,
    RECEIVED,
    PARTIAL,
    REJECTED,
  }

  /**
   * Unique identifier for the purchase order item.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Reference to the purchase order this item belongs to.
   */
  @ManyToOne
  @JoinColumn(name = "purchase_order_id", nullable = false)
  private PurchaseOrder purchaseOrder;

  /**
   * Reference to the product being ordered.
   */
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  /**
   * Quantity of the product ordered.
   */
  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer quantity;

  /**
   * Unit price of the product.
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  /**
   * Total price for this item (quantity * unitPrice).
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "total_price", nullable = false)
  @Builder.Default
  private BigDecimal totalPrice = BigDecimal.ZERO;

  /**
   * Quantity received so far.
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "received_quantity", nullable = false)
  @Builder.Default
  private Integer receivedQuantity = 0;

  /**
   * Current status of the item.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Status status = Status.PENDING;

  /**
   * Additional notes about the item.
   */
  @Column(length = 500)
  private String notes;

  /**
   * Timestamp of when the item was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the item was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Calculate the total price for this item.
   * This method should be called before saving to ensure the total price is correct.
   */
  public void calculateTotalPrice() {
    if (quantity != null && unitPrice != null) {
      this.totalPrice = unitPrice.multiply(new BigDecimal(quantity));
    }
  }
}
