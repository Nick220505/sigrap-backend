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
import jakarta.validation.constraints.NotEmpty;
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
 * Entity representing a sales return transaction in the system.
 *
 * <p>A sales return represents a transaction where one or more products from a previous sale are returned.
 * It records details including the original sale, customer, employee, and the total amount of the return.</p>
 */
@Entity
@Table(name = "sale_returns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturn {

  /**
   * Unique identifier for the sales return.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Reference to the original sale from which items are being returned.
   */
  @NotNull(message = "Original sale cannot be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_sale_id", nullable = false)
  private Sale originalSale;

  /**
   * The total amount of the return. This is typically the sum of the subtotals of returned items.
   */
  @NotNull(message = "Total return amount cannot be null")
  @PositiveOrZero(message = "Total return amount must be zero or positive")
  @Column(name = "total_return_amount", nullable = false)
  private BigDecimal totalReturnAmount;

  /**
   * Reference to the customer who is returning the items.
   * This should be the same customer as in the original sale.
   */
  @NotNull(message = "Customer cannot be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  /**
   * Reference to the employee who processed the return.
   */
  @NotNull(message = "Employee cannot be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private User employee;

  /**
   * The items included in this sales return.
   */
  @OneToMany(
    mappedBy = "saleReturn",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @Builder.Default
  private List<SaleReturnItem> items = new ArrayList<>();

  /**
   * Reason for the return.
   */
  @NotNull(message = "Reason cannot be null")
  @NotEmpty(message = "Reason cannot be empty")
  @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
  private String reason;

  /**
   * The date and time when the sales return was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * The date and time when the sales return was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Add an item to this sales return.
   *
   * @param item The sales return item to add
   * @return The current sales return instance
   */
  public SaleReturn addItem(SaleReturnItem item) {
    if (items == null) {
      items = new ArrayList<>();
    }
    items.add(item);
    item.setSaleReturn(this);
    return this;
  }

  /**
   * Remove an item from this sales return.
   *
   * @param item The sales return item to remove
   * @return The current sales return instance
   */
  public SaleReturn removeItem(SaleReturnItem item) {
    if (items != null) {
      items.remove(item);
      item.setSaleReturn(null);
    }
    return this;
  }
}
