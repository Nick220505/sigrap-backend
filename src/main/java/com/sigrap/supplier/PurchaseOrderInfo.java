package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing purchase order information.
 * Used for returning purchase order data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents purchase order read operations</li>
 *   <li>Includes complete order details</li>
 *   <li>Contains supplier information</li>
 *   <li>Includes item details</li>
 *   <li>Tracks audit timestamps</li>
 * </ul></p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Immutable order data</li>
 *   <li>Supplier association</li>
 *   <li>Order items list</li>
 *   <li>Audit trail</li>
 * </ul></p>
 *
 * @see PurchaseOrderController
 * @see PurchaseOrderService
 * @see PurchaseOrderMapper
 * @see SupplierInfo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing purchase order information")
public class PurchaseOrderInfo {

  /**
   * The unique identifier of the purchase order.
   */
  @Schema(
    description = "Unique identifier of the purchase order",
    example = "1"
  )
  private Integer id;

  /**
   * The order number for tracking purposes.
   */
  @Schema(
    description = "Order number for tracking purposes",
    example = "ORD-001"
  )
  private String orderNumber;

  /**
   * Information about the supplier for this order.
   */
  @Schema(description = "Information about the supplier for this order")
  private SupplierInfo supplier;

  /**
   * The date when the order was placed.
   */
  @Schema(
    description = "Date when the order was placed",
    example = "2023-05-15"
  )
  private LocalDate orderDate;

  /**
   * The expected delivery date for the order.
   */
  @Schema(
    description = "Expected delivery date for the order",
    example = "2023-05-25"
  )
  private LocalDate expectedDeliveryDate;

  /**
   * The actual delivery date when the order was received.
   */
  @Schema(
    description = "Actual delivery date when the order was received",
    example = "2023-05-23"
  )
  private LocalDate actualDeliveryDate;

  /**
   * The current status of the order.
   */
  @Schema(description = "Current status of the order", example = "CONFIRMED")
  private String status;

  /**
   * The total amount of the order.
   */
  @Schema(description = "Total amount of the order", example = "1250.00")
  private BigDecimal totalAmount;

  /**
   * Additional notes about the order.
   */
  @Schema(
    description = "Additional notes about the order",
    example = "Urgent order for school supplies"
  )
  private String notes;

  /**
   * The list of items in this order.
   */
  @Schema(description = "List of items in this order")
  private List<PurchaseOrderItemInfo> items;

  /**
   * The timestamp when the order was created.
   */
  @Schema(
    description = "Timestamp when the order was created",
    example = "2023-05-15T10:30:00"
  )
  private LocalDateTime createdAt;

  /**
   * The timestamp when the order was last updated.
   */
  @Schema(
    description = "Timestamp when the order was last updated",
    example = "2023-05-16T14:45:00"
  )
  private LocalDateTime updatedAt;
}
