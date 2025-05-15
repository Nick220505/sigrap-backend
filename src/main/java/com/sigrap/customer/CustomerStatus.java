package com.sigrap.customer;

/**
 * Enum representing the possible statuses of a customer.
 * Used to track the current state of a customer account.
 */
public enum CustomerStatus {
  /**
   * Customer is currently active.
   */
  ACTIVE,

  /**
   * Customer is temporarily inactive.
   */
  INACTIVE,

  /**
   * Customer has been blocked.
   */
  BLOCKED,
}
