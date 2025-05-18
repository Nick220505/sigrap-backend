package com.sigrap.payment;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
 *   <li>{@code amount}: Amount of the payment. (Required, must be positive).</li>
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

  @NotNull(message = "Amount cannot be null")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private BigDecimal amount;
}
