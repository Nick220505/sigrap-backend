package com.sigrap.supplier;

/**
 * Enumeration of payment methods accepted by suppliers.
 * Used to standardize payment method selection across the application.
 */
public enum PaymentMethod {
  /**
   * Credit card payment method.
   */
  CREDIT_CARD,

  /**
   * Bank transfer payment method.
   */
  BANK_TRANSFER,

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
   * Payment on delivery method.
   */
  PAYMENT_ON_DELIVERY,

  /**
   * Deferred payment method (e.g., Net 30, Net 60).
   */
  DEFERRED_PAYMENT,
}
