package com.sigrap.supplier;

/**
 * Enum defining possible statuses for purchase orders.
 * This enum represents all possible states a purchase order can be in during its lifecycle.
 */
public enum PurchaseOrderStatus {
  /**
   * Order is in draft state and not yet submitted to the supplier.
   */
  DRAFT,

  /**
   * Order has been submitted to the supplier but not yet confirmed.
   */
  SUBMITTED,

  /**
   * Order has been confirmed by the supplier.
   */
  CONFIRMED,

  /**
   * Order is being processed by the supplier.
   */
  IN_PROCESS,

  /**
   * Order has been shipped by the supplier.
   */
  SHIPPED,

  /**
   * Order has been delivered but not yet paid.
   */
  DELIVERED,

  /**
   * Order has been cancelled.
   */
  CANCELLED,

  /**
   * Order has been paid in full.
   */
  PAID,
}
