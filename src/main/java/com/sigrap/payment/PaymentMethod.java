package com.sigrap.payment;

/**
 * Enumeration of payment methods supported by the system.
 * Used for payments to suppliers and from customers.
 *
 * <p>This enum standardizes the acceptable payment methods across the application,
 * ensuring consistent data for reporting and analysis.</p>
 */
public enum PaymentMethod {
  /**
   * Payment through direct bank transfer.
   */
  BANK_TRANSFER,

  /**
   * Payment via credit card.
   */
  CREDIT_CARD,

  /**
   * Payment with physical cash.
   */
  CASH,

  /**
   * Payment by paper check.
   */
  CHECK,

  /**
   * Payment through PayPal service.
   */
  PAYPAL,

  /**
   * Payment through Nequi mobile payment platform.
   */
  NEQUI,

  /**
   * Payment through DaviPlata mobile payment platform.
   */
  DAVIPLATA,

  /**
   * Any other payment method not explicitly listed.
   */
  OTHER,
}
