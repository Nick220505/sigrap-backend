package com.sigrap.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating or updating a sales return.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a sales return")
public class SaleReturnData {

  /**
   * The ID of the original sale from which items are being returned.
   */
  @NotNull(message = "Original Sale ID cannot be null")
  @Schema(description = "ID of the original sale", example = "1")
  private Integer originalSaleId;

  /**
   * The total amount of the return.
   */
  @NotNull(message = "Total return amount cannot be null")
  @PositiveOrZero(message = "Total return amount must be zero or positive")
  @Schema(description = "Total amount of the return", example = "50.00")
  private BigDecimal totalReturnAmount;

  /**
   * ID of the customer who is returning the items.
   */
  @NotNull(message = "Customer ID cannot be null")
  @Schema(description = "ID of the customer", example = "1")
  private Long customerId;

  /**
   * ID of the employee who processed the return.
   */
  @NotNull(message = "Employee ID cannot be null")
  @Schema(
    description = "ID of the employee who processed the return",
    example = "1"
  )
  private Long employeeId;

  /**
   * The items included in this sales return.
   */
  @NotEmpty(message = "Sales return must have at least one item")
  @Valid
  @Schema(description = "List of items in the sales return")
  private List<SaleReturnItemData> items;

  /**
   * Reason for the return.
   */
  @NotNull(message = "Reason cannot be null")
  @NotEmpty(message = "Reason cannot be empty")
  @Schema(description = "Reason for the return", example = "Product damaged")
  private String reason;
}
