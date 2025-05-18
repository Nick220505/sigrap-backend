package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating purchase orders.
 * Contains validated purchase order data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates purchase order input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages item lists</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Supplier ID must not be null for creation</li>
 *   <li>Items should be valid if provided</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * PurchaseOrderData orderData = PurchaseOrderData.builder()
 *     .supplierId(1L)
 *     .deliveryDate(LocalDate.now().plusWeeks(1))
 *     .items(List.of(orderItem1, orderItem2))
 *     .build();
 * </pre></p>
 *
 * @see PurchaseOrderController
 * @see PurchaseOrderService
 * @see PurchaseOrder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating a purchase order"
)
public class PurchaseOrderData {

  /**
   * The ID of the supplier for this order.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be null for order creation</li>
   *   <li>Must reference an existing supplier</li>
   * </ul></p>
   */
  @NotNull(message = "Supplier ID cannot be null")
  @Schema(description = "ID of the supplier for this order", example = "1")
  private Long supplierId;

  /**
   * The delivery date for the order.
   *
   * <p>Properties:
   * <ul>
   *   <li>Optional field</li>
   *   <li>Should be a future date</li>
   * </ul></p>
   */
  @Schema(description = "Delivery date for the order", example = "2023-05-25")
  private LocalDate deliveryDate;

  /**
   * The list of items in this order.
   *
   * <p>Properties:
   * <ul>
   *   <li>Each item must be valid</li>
   *   <li>Items can be added/removed in updates</li>
   * </ul></p>
   */
  @Valid
  @Schema(description = "List of items in this order")
  private List<PurchaseOrderItemData> items;
}
