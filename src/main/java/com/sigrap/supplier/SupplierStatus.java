package com.sigrap.supplier;

/**
 * Enum representing the possible statuses of a supplier relationship.
 * Used to track the current state of business relationships with suppliers.
 */
public enum SupplierStatus {
  /**
   * Active and fully operational supplier relationship.
   */
  ACTIVE,

  /**
   * Supplier relationship is inactive or on hold.
   */
  INACTIVE,

  /**
   * Supplier is in a probationary period, often for new suppliers.
   */
  PROBATION,

  /**
   * Supplier relationship has been terminated.
   */
  TERMINATED,

  /**
   * Supplier is blacklisted and should not be used.
   */
  BLACKLISTED,
}
