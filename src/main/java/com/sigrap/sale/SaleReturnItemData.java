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
 * Data Transfer Object for creating or updating a sales return item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for a sales return item")
public class SaleReturnItemData {

  /**
   * The ID of the product being returned.
   */
  @NotNull(message = "Product ID cannot be null")
  @Schema(description = "ID of the product being returned", example = "1")
  private Integer productId;

  /**
   * The quantity of the product being returned.
   */
  @NotNull(message = "Quantity cannot be null")
  @Positive(message = "Quantity must be positive")
  @Schema(description = "Quantity of the product being returned", example = "1")
  private Integer quantity;

  /**
   * The unit price of the product at the time of the original sale.
   */
  @NotNull(message = "Unit price cannot be null")
  @PositiveOrZero(message = "Unit price must be zero or positive")
  @Schema(description = "Unit price at original sale", example = "10.50")
  private BigDecimal unitPrice;

  /**
   * The subtotal for this returned item (quantity * unit price).
   */
  @NotNull(message = "Subtotal cannot be null")
  @PositiveOrZero(message = "Subtotal must be zero or positive")
  @Schema(description = "Subtotal for this returned item", example = "10.50")
  private BigDecimal subtotal;
}
