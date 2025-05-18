package com.sigrap.sale;

import com.sigrap.product.ProductInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning sales return item information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales return item information")
public class SaleReturnItemInfo {

  /**
   * The unique identifier of the sales return item.
   */
  @Schema(description = "ID of the sales return item", example = "1")
  private Integer id;

  /**
   * Information about the product returned.
   */
  @Schema(description = "Product information")
  private ProductInfo product;

  /**
   * The quantity of the product returned.
   */
  @Schema(description = "Quantity of the product returned", example = "1")
  private Integer quantity;

  /**
   * The unit price of the product at the time of the original sale.
   */
  @Schema(description = "Unit price at original sale", example = "10.50")
  private BigDecimal unitPrice;

  /**
   * The subtotal for this returned item (quantity * unit price).
   */
  @Schema(description = "Subtotal for this returned item", example = "10.50")
  private BigDecimal subtotal;
}
