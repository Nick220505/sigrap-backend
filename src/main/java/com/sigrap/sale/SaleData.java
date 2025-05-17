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
 * Data Transfer Object for creating or updating a sale.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a sale")
public class SaleData {

  /**
   * The total amount of the sale before taxes and discounts.
   */
  @NotNull(message = "Total amount cannot be null")
  @PositiveOrZero(message = "Total amount must be zero or positive")
  @Schema(description = "Total amount of the sale", example = "100.00")
  private BigDecimal totalAmount;

  /**
   * The amount of tax applied to the sale.
   */
  @NotNull(message = "Tax amount cannot be null")
  @PositiveOrZero(message = "Tax amount must be zero or positive")
  @Schema(description = "Tax amount applied to the sale", example = "19.00")
  private BigDecimal taxAmount;

  /**
   * The discount applied to the sale, if any.
   */
  @PositiveOrZero(message = "Discount amount must be zero or positive")
  @Schema(
    description = "Discount amount applied to the sale",
    example = "10.00"
  )
  private BigDecimal discountAmount;

  /**
   * The final amount to be paid after applying discounts and taxes.
   */
  @NotNull(message = "Final amount cannot be null")
  @PositiveOrZero(message = "Final amount must be zero or positive")
  @Schema(description = "Final amount to be paid", example = "109.00")
  private BigDecimal finalAmount;

  /**
   * ID of the customer who made the purchase.
   */
  @NotNull(message = "Customer ID cannot be null")
  @Schema(description = "ID of the customer", example = "1")
  private Long customerId;

  /**
   * ID of the employee who processed the sale.
   */
  @NotNull(message = "Employee ID cannot be null")
  @Schema(
    description = "ID of the employee who processed the sale",
    example = "1"
  )
  private Long employeeId;

  /**
   * The items included in this sale.
   */
  @NotEmpty(message = "Sale must have at least one item")
  @Valid
  @Schema(description = "List of items in the sale")
  private List<SaleItemData> items;
}
