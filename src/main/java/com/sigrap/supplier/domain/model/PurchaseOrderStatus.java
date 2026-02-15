package com.sigrap.supplier.domain.model;

/**
 * Enum defining possible statuses for purchase orders in the domain layer.
 * This enum represents all possible states a purchase order can be in during its lifecycle.
 */
public enum PurchaseOrderStatus {
    /**
     * Order is pending approval or processing.
     */
    PENDING,

    /**
     * Order has been approved and is ready to be sent to supplier.
     */
    APPROVED,

    /**
     * Order has been received from the supplier.
     */
    RECEIVED,

    /**
     * Order has been cancelled.
     */
    CANCELLED
}
