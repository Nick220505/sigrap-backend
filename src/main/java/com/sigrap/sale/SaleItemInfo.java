package com.sigrap.sale;

import com.sigrap.product.ProductInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning sale item information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sale item information")
public class SaleItemInfo {

  /**
   * The unique identifier of the sale item.
   */
  @Schema(description = "ID of the sale item", example = "1")
  private Integer id;

  /**
   * Information about the product sold.
   */
  @Schema(description = "Product information")
  private ProductInfo product;

  /**
   * The quantity of the product sold.
   */
  @Schema(description = "Quantity of the product", example = "2")
  private Integer quantity;

  /**
   * The unit price of the product at the time of sale.
   */
  @Schema(description = "Unit price of the product", example = "10.50")
  private BigDecimal unitPrice;

  /**
   * The subtotal for this item (quantity * unit price - discount).
   */
  @Schema(description = "Subtotal for this item", example = "20.00")
  private BigDecimal subtotal;
}
