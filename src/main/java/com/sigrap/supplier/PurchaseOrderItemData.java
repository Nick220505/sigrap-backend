package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating purchase order items.
 * Contains validated purchase order item data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates purchase order item input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Product ID must not be null</li>
 *   <li>Quantity must be positive</li>
 *   <li>Unit price must be zero or positive</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * PurchaseOrderItemData itemData = PurchaseOrderItemData.builder()
 *     .productId(1)
 *     .quantity(10)
 *     .unitPrice(new BigDecimal("12.50"))
 *     .build();
 * </pre></p>
 *
 * @see PurchaseOrderItem
 * @see PurchaseOrderData
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating a purchase order item"
)
public class PurchaseOrderItemData {

  /**
   * The ID of the item (for updates only).
   */
  @Schema(description = "ID of the item (for updates only)", example = "1")
  private Integer id;

  /**
   * The ID of the product being ordered.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must not be null</li>
   *   <li>Must reference an existing product</li>
   * </ul></p>
   */
  @NotNull(message = "Product ID cannot be null")
  @Schema(description = "ID of the product being ordered", example = "1")
  private Integer productId;

  /**
   * The quantity of the product ordered.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must be positive</li>
   * </ul></p>
   */
  @NotNull(message = "Quantity cannot be null")
  @Positive(message = "Quantity must be positive")
  @Schema(description = "Quantity of the product ordered", example = "10")
  private Integer quantity;

  /**
   * The unit price of the product.
   *
   * <p>Validation:
   * <ul>
   *   <li>Must be zero or positive</li>
   * </ul></p>
   */
  @NotNull(message = "Unit price cannot be null")
  @PositiveOrZero(message = "Unit price must be zero or positive")
  @Schema(description = "Unit price of the product", example = "12.50")
  private BigDecimal unitPrice;
}
