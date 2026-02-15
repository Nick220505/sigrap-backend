package com.sigrap.sale.domain.model;

/**
 * Enumeration representing payment methods for sales.
 */
public enum PaymentMethod {
    /**
     * Payment made in cash.
     */
    CASH,
    
    /**
     * Payment made by credit card.
     */
    CREDIT_CARD,
    
    /**
     * Payment made by debit card.
     */
    DEBIT_CARD,
    
    /**
     * Payment made via bank transfer.
     */
    BANK_TRANSFER,
    
    /**
     * Other payment method.
     */
    OTHER
}
