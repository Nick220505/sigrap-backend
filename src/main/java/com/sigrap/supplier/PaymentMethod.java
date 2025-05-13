package com.sigrap.supplier;

/**
 * Enum representing payment methods accepted by suppliers.
 * Used to standardize payment method options in the system.
 */
public enum PaymentMethod {
  /**
   * Bank transfer payment method.
   */
  BANK_TRANSFER,

  /**
   * Credit card payment method.
   */
  CREDIT_CARD,

  /**
   * Cash payment method.
   */
  CASH,

  /**
   * Check payment method.
   */
  CHECK,

  /**
   * PayPal payment method.
   */
  PAYPAL,

  /**
   * Other payment methods not listed above.
   */
  OTHER,
}
