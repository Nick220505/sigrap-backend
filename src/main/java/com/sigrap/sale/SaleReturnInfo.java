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
 * Data Transfer Object for returning sales return information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales return information")
public class SaleReturnInfo {

  /**
   * The unique identifier of the sales return.
   */
  @Schema(description = "ID of the sales return", example = "1")
  private Integer id;

  /**
   * Information about the original sale.
   */
  @Schema(description = "Original sale ID")
  private Integer originalSaleId;

  /**
   * The total amount of the return.
   */
  @Schema(description = "Total amount of the return", example = "50.00")
  private BigDecimal totalReturnAmount;

  /**
   * Information about the customer who returned the items.
   */
  @Schema(description = "Customer information")
  private CustomerInfo customer;

  /**
   * Information about the employee who processed the return.
   */
  @Schema(description = "Employee information")
  private UserInfo employee;

  /**
   * The items included in this sales return.
   */
  @Schema(description = "List of items in the sales return")
  private List<SaleReturnItemInfo> items;

  /**
   * Reason for the return.
   */
  @Schema(description = "Reason for the return", example = "Product damaged")
  private String reason;

  /**
   * The date and time when the sales return was created.
   */
  @Schema(
    description = "Date and time of return creation",
    example = "2023-04-16T10:00:00"
  )
  private LocalDateTime createdAt;

  /**
   * The date and time when the sales return was last updated.
   */
  @Schema(
    description = "Date and time of last update",
    example = "2023-04-16T10:05:00"
  )
  private LocalDateTime updatedAt;
}
