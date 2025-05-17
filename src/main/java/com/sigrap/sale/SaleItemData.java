package com.sigrap.sale;

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
 * Data Transfer Object for creating or updating a sale item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for a sale item")
public class SaleItemData {

  /**
   * The ID of the product being sold.
   */
  @NotNull(message = "Product ID cannot be null")
  @Schema(description = "ID of the product", example = "1")
  private Integer productId;

  /**
   * The quantity of the product being sold.
   */
  @NotNull(message = "Quantity cannot be null")
  @Positive(message = "Quantity must be positive")
  @Schema(description = "Quantity of the product", example = "2")
  private Integer quantity;

  /**
   * The unit price of the product at the time of sale.
   */
  @NotNull(message = "Unit price cannot be null")
  @PositiveOrZero(message = "Unit price must be zero or positive")
  @Schema(description = "Unit price of the product", example = "10.50")
  private BigDecimal unitPrice;

  /**
   * The subtotal for this item (quantity * unit price - discount).
   */
  @NotNull(message = "Subtotal cannot be null")
  @PositiveOrZero(message = "Subtotal must be zero or positive")
  @Schema(description = "Subtotal for this item", example = "20.00")
  private BigDecimal subtotal;
}
