package com.sigrap.sale;

import com.sigrap.customer.Customer;
import com.sigrap.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entity representing a sale transaction in the system.
 *
 * <p>A sale represents a transaction where one or more products are sold to a customer.
 * It records details including the total amount, employee who processed the sale,
 * and the items included in the sale.</p>
 */
@Entity
@Table(name = "sales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

  /**
   * Unique identifier for the sale.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The total amount of the sale.
   */
  @NotNull(message = "Total amount cannot be null")
  @PositiveOrZero(message = "Total amount must be zero or positive")
  @Column(nullable = false)
  private BigDecimal totalAmount;

  /**
   * The amount of tax applied to the sale.
   */
  @NotNull(message = "Tax amount cannot be null")
  @PositiveOrZero(message = "Tax amount must be zero or positive")
  @Column(name = "tax_amount", nullable = false)
  private BigDecimal taxAmount;

  /**
   * The discount applied to the sale, if any.
   */
  @NotNull(message = "Discount amount cannot be null")
  @PositiveOrZero(message = "Discount amount must be zero or positive")
  @Column(name = "discount_amount", nullable = false)
  @Builder.Default
  private BigDecimal discountAmount = BigDecimal.ZERO;

  /**
   * The final amount to be paid after applying discounts and taxes.
   */
  @NotNull(message = "Final amount cannot be null")
  @PositiveOrZero(message = "Final amount must be zero or positive")
  @Column(name = "final_amount", nullable = false)
  private BigDecimal finalAmount;

  /**
   * Reference to the customer who made the purchase.
   */
  @NotNull(message = "Customer cannot be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  /**
   * Reference to the employee who processed the sale.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private User employee;

  /**
   * The items included in this sale.
   */
  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<SaleItem> items = new ArrayList<>();

  /**
   * The date and time when the sale was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * The date and time when the sale was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Add an item to this sale.
   *
   * @param item The sale item to add
   * @return The current sale instance
   */
  public Sale addItem(SaleItem item) {
    if (items == null) {
      items = new ArrayList<>();
    }
    items.add(item);
    item.setSale(this);
    return this;
  }

  /**
   * Remove an item from this sale.
   *
   * @param item The sale item to remove
   * @return The current sale instance
   */
  public Sale removeItem(SaleItem item) {
    if (items != null) {
      items.remove(item);
      item.setSale(null);
    }
    return this;
  }
}
