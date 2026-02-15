package com.sigrap.sale.domain.model;

/**
 * Enumeration representing the status of a sale.
 */
public enum SaleStatus {
    /**
     * Sale is pending completion.
     */
    PENDING,
    
    /**
     * Sale has been completed successfully.
     */
    COMPLETED,
    
    /**
     * Sale has been cancelled.
     */
    CANCELLED
}
