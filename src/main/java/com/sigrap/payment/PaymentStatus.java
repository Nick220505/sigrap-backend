package com.sigrap.payment;

/**
 * Represents the status of a payment.
 *
 * <p>This enum defines the various states a payment can be in throughout its lifecycle.</p>
 * <ul>
 *   <li>{@code PENDING}: The payment is awaiting processing or action.</li>
 *   <li>{@code PROCESSING}: The payment is currently being processed.</li>
 *   <li>{@code COMPLETED}: The payment has been successfully processed.</li>
 *   <li>{@code FAILED}: The payment processing attempt failed.</li>
 *   <li>{@code CANCELLED}: The payment has been cancelled.</li>
 *   <li>{@code OVERDUE}: The payment has passed its due date and is not yet completed.</li>
 * </ul>
 */
public enum PaymentStatus {
  /**
   * The payment is awaiting processing or action.
   */
  PENDING,

  /**
   * The payment is currently being processed.
   */
  PROCESSING,

  /**
   * The payment has been successfully processed.
   */
  COMPLETED,

  /**
   * The payment processing attempt failed.
   */
  FAILED,

  /**
   * The payment has been cancelled.
   */
  CANCELLED,

  /**
   * The payment has passed its due date and is not yet completed.
   */
  OVERDUE,
}
