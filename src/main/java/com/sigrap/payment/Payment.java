package com.sigrap.payment;

import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Represents a payment made for a purchase order to a supplier.
 *
 * <p>This entity stores details about payments, including the amount.
 * It is linked to a specific {@link PurchaseOrder} and {@link Supplier}.</p>
 *
 * <p>Key attributes:
 * <ul>
 *   <li>{@code id}: The unique identifier for the payment.</li>
 *   <li>{@code purchaseOrder}: The purchase order associated with this payment.</li>
 *   <li>{@code supplier}: The supplier to whom the payment is made.</li>
 *   <li>{@code amount}: The monetary amount of the payment.</li>
 *   <li>{@code createdAt}: Timestamp of when the payment record was created.</li>
 *   <li>{@code updatedAt}: Timestamp of when the payment record was last updated.</li>
 * </ul>
 * </p>
 *
 * @see PurchaseOrder
 * @see Supplier
 */
@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  /**
   * The unique identifier for the payment.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The purchase order associated with this payment.
   * This indicates which order this payment is for.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "purchase_order_id")
  private PurchaseOrder purchaseOrder;

  /**
   * The supplier to whom the payment is made.
   * While this can be derived from the purchase order, it's included for direct querying and denormalization if needed.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id", nullable = false)
  private Supplier supplier;

  /**
   * The monetary amount of the payment.
   * Stored with precision suitable for currency values.
   */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  /**
   * Timestamp indicating when this payment record was created in the system.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp indicating the last time this payment record was updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
