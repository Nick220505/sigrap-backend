package com.sigrap.sale;

import com.sigrap.customer.CustomerInfo;
import com.sigrap.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning sale information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sale information")
public class SaleInfo {

  /**
   * The unique identifier of the sale.
   */
  @Schema(description = "ID of the sale", example = "1")
  private Integer id;

  /**
   * The total amount of the sale before taxes and discounts.
   */
  @Schema(description = "Total amount of the sale", example = "100.00")
  private BigDecimal totalAmount;

  /**
   * The amount of tax applied to the sale.
   */
  @Schema(description = "Tax amount applied to the sale", example = "19.00")
  private BigDecimal taxAmount;

  /**
   * The discount applied to the sale, if any.
   */
  @Schema(
    description = "Discount amount applied to the sale",
    example = "10.00"
  )
  private BigDecimal discountAmount;

  /**
   * The final amount to be paid after applying discounts and taxes.
   */
  @Schema(description = "Final amount to be paid", example = "109.00")
  private BigDecimal finalAmount;

  /**
   * Any additional notes related to the sale.
   */
  @Schema(
    description = "Additional notes about the sale",
    example = "Customer requested fast delivery"
  )
  private String notes;

  /**
   * The payment method used for the sale.
   */
  @Schema(description = "Payment method used", example = "CREDIT_CARD")
  private PaymentMethod paymentMethod;

  /**
   * The status of the sale.
   */
  @Schema(description = "Status of the sale", example = "COMPLETED")
  private SaleStatus status;

  /**
   * Information about the customer who made the purchase.
   */
  @Schema(description = "Customer information")
  private CustomerInfo customer;

  /**
   * Information about the employee who processed the sale.
   */
  @Schema(description = "Employee information")
  private UserInfo employee;

  /**
   * The items included in this sale.
   */
  @Schema(description = "List of items in the sale")
  private List<SaleItemInfo> items;

  /**
   * The date and time when the sale was created.
   */
  @Schema(
    description = "Date and time when the sale was created",
    example = "2023-04-15T14:30:00"
  )
  private LocalDateTime createdAt;

  /**
   * The date and time when the sale was last updated.
   */
  @Schema(
    description = "Date and time when the sale was last updated",
    example = "2023-04-15T14:35:00"
  )
  private LocalDateTime updatedAt;
}
