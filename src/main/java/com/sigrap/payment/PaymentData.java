package com.sigrap.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating or updating Payment information.
 *
 * <p>This DTO is used to transfer data from the client to the server when creating a new payment
 * or updating an existing one. It includes validation constraints to ensure data integrity.</p>
 *
 * <p>Fields include:
 * <ul>
 *   <li>{@code purchaseOrderId}: ID of the purchase order this payment is for. (Optional for updates if not changing PO).</li>
 *   <li>{@code supplierId}: ID of the supplier. (Required).</li>
 *   <li>{@code paymentDate}: Date of the payment. (Optional, defaults to today if not provided on creation).</li>
 *   <li>{@code amount}: Amount of the payment. (Required, must be positive).</li>
 *   <li>{@code paymentMethod}: Method used for payment. (Required).</li>
 *   <li>{@code status}: Status of the payment. (Required).</li>
 *   <li>{@code invoiceNumber}: Invoice number. (Required).</li>
 *   <li>{@code dueDate}: Due date for the payment. (Required).</li>
 *   <li>{@code transactionId}: Optional transaction ID.</li>
 *   <li>{@code notes}: Optional notes.</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentData {

  private Integer purchaseOrderId;

  @NotNull(message = "Supplier ID cannot be null")
  @Positive(message = "Supplier ID must be a positive number")
  private Long supplierId;

  private LocalDate paymentDate;

  @NotNull(message = "Amount cannot be null")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private BigDecimal amount;

  @NotNull(message = "Payment method cannot be null")
  private PaymentMethod paymentMethod;

  @NotNull(message = "Payment status cannot be null")
  private PaymentStatus status;

  @NotNull(message = "Invoice number cannot be null")
  @Size(
    min = 1,
    max = 50,
    message = "Invoice number must be between 1 and 50 characters"
  )
  private String invoiceNumber;

  @NotNull(message = "Due date cannot be null")
  private LocalDate dueDate;

  @Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
  private String transactionId;

  @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
  private String notes;
}
