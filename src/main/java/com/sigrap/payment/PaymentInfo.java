package com.sigrap.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 *   <li>{@code paymentDate}: The date of the payment.</li>
 *   <li>{@code amount}: The amount of the payment.</li>
 *   <li>{@code paymentMethod}: The method used for payment.</li>
 *   <li>{@code status}: The current status of the payment.</li>
 *   <li>{@code invoiceNumber}: The invoice number.</li>
 *   <li>{@code dueDate}: The due date of the payment.</li>
 *   <li>{@code transactionId}: Optional transaction ID.</li>
 *   <li>{@code notes}: Optional notes.</li>
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
  private LocalDate paymentDate;
  private BigDecimal amount;
  private PaymentMethod paymentMethod;
  private PaymentStatus status;
  private String invoiceNumber;
  private LocalDate dueDate;
  private String transactionId;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
