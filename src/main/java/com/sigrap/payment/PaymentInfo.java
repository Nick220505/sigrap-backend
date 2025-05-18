package com.sigrap.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for sending Payment information to the client.
 *
 * <p>This DTO represents the structure of payment data as it is exposed through the API.
 * It includes essential payment details along with related information like supplier name
 * and purchase order number for display purposes.</p>
 *
 * <p>Fields include:
 * <ul>
 *   <li>{@code id}: The unique identifier of the payment.</li>
 *   <li>{@code purchaseOrderId}: The ID of the associated purchase order.</li>
 *   <li>{@code purchaseOrderNumber}: The number of the associated purchase order.</li>
 *   <li>{@code supplierId}: The ID of the supplier.</li>
 *   <li>{@code supplierName}: The name of the supplier.</li>
 *   <li>{@code amount}: The amount of the payment.</li>
 *   <li>{@code createdAt}: Creation timestamp.</li>
 *   <li>{@code updatedAt}: Last update timestamp.</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {

  private Integer id;
  private Integer purchaseOrderId;
  private String purchaseOrderNumber;
  private Long supplierId;
  private String supplierName;
  private BigDecimal amount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
