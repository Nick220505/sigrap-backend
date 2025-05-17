package com.sigrap.sale;

/**
 * Enum representing the possible statuses of a sale.
 */
public enum SaleStatus {
  /**
   * Sale has been completed successfully.
   */
  COMPLETED,

  /**
   * Sale is in progress.
   */
  IN_PROGRESS,

  /**
   * Sale has been cancelled.
   */
  CANCELLED,

  /**
   * Sale has been returned (all items returned).
   */
  RETURNED,

  /**
   * Sale has been partially returned (some items returned).
   */
  PARTIALLY_RETURNED,
}
