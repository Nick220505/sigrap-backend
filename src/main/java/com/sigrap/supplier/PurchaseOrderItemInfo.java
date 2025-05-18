package com.sigrap.supplier;

import com.sigrap.product.ProductInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing purchase order item information.
 * Used for returning purchase order item data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents purchase order item read operations</li>
 *   <li>Includes complete item details</li>
 *   <li>Contains product information</li>
 *   <li>Tracks audit timestamps</li>
 * </ul></p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Immutable item data</li>
 *   <li>Product association</li>
 *   <li>Price and quantity information</li>
 *   <li>Audit trail</li>
 * </ul></p>
 *
 * @see PurchaseOrderItem
 * @see PurchaseOrderInfo
 * @see ProductInfo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Response object containing purchase order item information"
)
public class PurchaseOrderItemInfo {

  /**
   * The unique identifier of the purchase order item.
   */
  @Schema(
    description = "Unique identifier of the purchase order item",
    example = "1"
  )
  private Integer id;

  /**
   * The ID of the purchase order this item belongs to.
   */
  @Schema(
    description = "ID of the purchase order this item belongs to",
    example = "1"
  )
  private Integer purchaseOrderId;

  /**
   * Information about the product being ordered.
   */
  @Schema(description = "Information about the product being ordered")
  private ProductInfo product;

  /**
   * The quantity of the product ordered.
   */
  @Schema(description = "Quantity of the product ordered", example = "10")
  private Integer quantity;

  /**
   * The unit price of the product.
   */
  @Schema(description = "Unit price of the product", example = "12.50")
  private BigDecimal unitPrice;

  /**
   * The total price for this item (quantity * unitPrice).
   */
  @Schema(description = "Total price for this item", example = "125.00")
  private BigDecimal totalPrice;

  /**
   * The quantity received so far.
   */
  @Schema(description = "Quantity received so far", example = "5")
  private Integer receivedQuantity;

  /**
   * The timestamp when the item was created.
   */
  @Schema(
    description = "Timestamp when the item was created",
    example = "2023-05-15T10:30:00"
  )
  private LocalDateTime createdAt;

  /**
   * The timestamp when the item was last updated.
   */
  @Schema(
    description = "Timestamp when the item was last updated",
    example = "2023-05-16T14:45:00"
  )
  private LocalDateTime updatedAt;
}
