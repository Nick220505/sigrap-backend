package com.sigrap.supplier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing a purchase order to a supplier.
 * Purchase orders are used to track orders placed with suppliers.
 */
@Entity
@Table(name = "purchase_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

  /**
   * Enum defining possible order statuses.
   */
  public enum Status {
    DRAFT,
    SUBMITTED,
    CONFIRMED,
    IN_PROCESS,
    SHIPPED,
    DELIVERED,
    CANCELLED,
  }

  /**
   * Unique identifier for the purchase order.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Order number for the purchase order.
   * Used for tracking and referencing orders.
   */
  @Column(name = "order_number", unique = true)
  private String orderNumber;

  /**
   * Reference to the supplier this order is placed with.
   */
  @ManyToOne
  @JoinColumn(name = "supplier_id", nullable = false)
  private Supplier supplier;

  /**
   * Date when the order was placed.
   */
  @NotNull
  @Column(name = "order_date", nullable = false)
  private LocalDate orderDate;

  /**
   * Expected delivery date for the order.
   */
  @Column(name = "expected_delivery_date")
  private LocalDate expectedDeliveryDate;

  /**
   * Actual delivery date when the order was received.
   */
  @Column(name = "actual_delivery_date")
  private LocalDate actualDeliveryDate;

  /**
   * Current status of the order.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Status status = Status.DRAFT;

  /**
   * Total amount of the order.
   */
  @PositiveOrZero
  @Column(name = "total_amount", nullable = false)
  @Builder.Default
  private BigDecimal totalAmount = BigDecimal.ZERO;

  /**
   * Additional notes about the order.
   */
  @Column(length = 1000)
  private String notes;

  /**
   * Items contained in this purchase order.
   */
  @OneToMany(
    mappedBy = "purchaseOrder",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  @Builder.Default
  private List<PurchaseOrderItem> items = new ArrayList<>();

  /**
   * Timestamp of when the purchase order was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the purchase order was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Helper method to add an item to this purchase order.
   *
   * @param item The item to add
   * @return This purchase order instance for method chaining
   */
  public PurchaseOrder addItem(PurchaseOrderItem item) {
    items.add(item);
    item.setPurchaseOrder(this);
    return this;
  }

  /**
   * Helper method to remove an item from this purchase order.
   *
   * @param item The item to remove
   * @return This purchase order instance for method chaining
   */
  public PurchaseOrder removeItem(PurchaseOrderItem item) {
    items.remove(item);
    item.setPurchaseOrder(null);
    return this;
  }
}
