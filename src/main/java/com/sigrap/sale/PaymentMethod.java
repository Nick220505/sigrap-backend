package com.sigrap.sale;

/**
 * Enum representing the possible payment methods for a sale.
 */
public enum PaymentMethod {
  /**
   * Cash payment.
   */
  CASH,

  /**
   * Credit card payment.
   */
  CREDIT_CARD,

  /**
   * Debit card payment.
   */
  DEBIT_CARD,

  /**
   * Bank transfer payment.
   */
  BANK_TRANSFER,

  /**
   * Mobile payment (e.g., Apple Pay, Google Pay).
   */
  MOBILE_PAYMENT,

  /**
   * Other payment methods not covered by the above.
   */
  OTHER,
}
