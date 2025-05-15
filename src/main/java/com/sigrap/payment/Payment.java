package com.sigrap.payment;

import com.sigrap.supplier.PaymentMethod;
import com.sigrap.supplier.PurchaseOrder;
import com.sigrap.supplier.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * <p>This entity stores details about payments, including the amount, date, method, and status.
 * It is linked to a specific {@link PurchaseOrder} and {@link Supplier}.</p>
 *
 * <p>Key attributes:
 * <ul>
 *   <li>{@code id}: The unique identifier for the payment.</li>
 *   <li>{@code purchaseOrder}: The purchase order associated with this payment.</li>
 *   <li>{@code supplier}: The supplier to whom the payment is made.</li>
 *   <li>{@code paymentDate}: The date when the payment was made or is scheduled.</li>
 *   <li>{@code amount}: The monetary amount of the payment.</li>
 *   <li>{@code paymentMethod}: The method used for the payment (e.g., BANK_TRANSFER, CREDIT_CARD).</li>
 *   <li>{@code status}: The current status of the payment (e.g., PENDING, COMPLETED).</li>
 *   <li>{@code invoiceNumber}: The invoice number related to this payment.</li>
 *   <li>{@code dueDate}: The date by which the payment is due.</li>
 *   <li>{@code transactionId}: An optional transaction identifier from the payment processor.</li>
 *   <li>{@code notes}: Optional notes or comments about the payment.</li>
 *   <li>{@code createdAt}: Timestamp of when the payment record was created.</li>
 *   <li>{@code updatedAt}: Timestamp of when the payment record was last updated.</li>
 * </ul>
 * </p>
 *
 * @see PurchaseOrder
 * @see Supplier
 * @see PaymentMethod
 * @see PaymentStatus
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
   * The date when the payment was actually made or is scheduled to be made.
   */
  @Column(name = "payment_date")
  private LocalDate paymentDate;

  /**
   * The monetary amount of the payment.
   * Stored with precision suitable for currency values.
   */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  /**
   * The method used for the payment, such as bank transfer, credit card, etc.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  private PaymentMethod paymentMethod;

  /**
   * The current status of the payment (e.g., PENDING, COMPLETED, FAILED).
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  /**
   * The invoice number associated with this payment.
   * This helps in reconciliation and tracking.
   */
  @Column(name = "invoice_number", length = 50)
  private String invoiceNumber;

  /**
   * The date by which the payment is due.
   * This is important for tracking overdue payments.
   */
  @Column(name = "due_date")
  private LocalDate dueDate;

  /**
   * An optional transaction identifier, typically provided by a payment gateway or bank.
   */
  @Column(name = "transaction_id", length = 100)
  private String transactionId;

  /**
   * Optional notes or comments related to the payment.
   */
  @Lob
  private String notes;

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
