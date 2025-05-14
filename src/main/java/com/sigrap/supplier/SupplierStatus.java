package com.sigrap.supplier;

/**
 * Enumeration of possible supplier relationship statuses.
 * Used to track the current state of business relationships with suppliers.
 */
public enum SupplierStatus {
  /**
   * Actively doing business with this supplier.
   */
  ACTIVE,

  /**
   * New supplier, not yet fully vetted or established.
   */
  NEW,

  /**
   * Temporarily not doing business with this supplier.
   */
  SUSPENDED,

  /**
   * No longer doing business with this supplier.
   */
  INACTIVE,

  /**
   * On probation due to quality issues or other concerns.
   */
  PROBATION,
}
