package com.sigrap.sale.domain.model;

/**
 * Enumeration representing the status of a sale return.
 */
public enum SaleReturnStatus {
    /**
     * Return request is pending review.
     */
    PENDING,
    
    /**
     * Return has been approved.
     */
    APPROVED,
    
    /**
     * Return has been rejected.
     */
    REJECTED,
    
    /**
     * Return has been completed and refund processed.
     */
    COMPLETED
}
