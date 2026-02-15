package com.sigrap.supplier.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a purchase order.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>Purchase orders are used to track orders placed with suppliers for products.
 * This entity maintains order information including status, dates, and amounts.</p>
 */
public class PurchaseOrder {
    private final PurchaseOrderId id;
    private final PurchaseOrderNumber orderNumber;
    private final SupplierId supplierId;
    private final LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private final BigDecimal totalAmount;
    private String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new purchase order (without ID).
     * Used when creating a purchase order before persistence.
     *
     * @param orderNumber the unique order number (required)
     * @param supplierId the supplier identifier (required)
     * @param orderDate the date the order was placed (required)
     * @param expectedDeliveryDate the expected delivery date (optional)
     * @param totalAmount the total amount of the order (required)
     * @param notes additional notes (optional)
     */
    public PurchaseOrder(PurchaseOrderNumber orderNumber, SupplierId supplierId,
                        LocalDate orderDate, LocalDate expectedDeliveryDate,
                        BigDecimal totalAmount, String notes) {
        this(null, orderNumber, supplierId, orderDate, expectedDeliveryDate,
             PurchaseOrderStatus.PENDING, totalAmount, notes,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a purchase order from persistence.
     *
     * @param id the purchase order identifier
     * @param orderNumber the unique order number (required)
     * @param supplierId the supplier identifier (required)
     * @param orderDate the date the order was placed (required)
     * @param expectedDeliveryDate the expected delivery date (optional)
     * @param status the current status (required)
     * @param totalAmount the total amount of the order (required)
     * @param notes additional notes (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public PurchaseOrder(PurchaseOrderId id, PurchaseOrderNumber orderNumber,
                        SupplierId supplierId, LocalDate orderDate,
                        LocalDate expectedDeliveryDate, PurchaseOrderStatus status,
                        BigDecimal totalAmount, String notes,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = Objects.requireNonNull(orderNumber, "Order number cannot be null");
        this.supplierId = Objects.requireNonNull(supplierId, "Supplier ID cannot be null");
        this.orderDate = Objects.requireNonNull(orderDate, "Order date cannot be null");
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        
        this.notes = notes;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Approves the purchase order.
     * Business rule: can only approve pending orders.
     *
     * @throws IllegalStateException if the order is not in PENDING status
     */
    public void approve() {
        if (status != PurchaseOrderStatus.PENDING) {
            throw new IllegalStateException("Can only approve pending purchase orders");
        }
        this.status = PurchaseOrderStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the purchase order as received.
     * Business rule: can only receive approved orders.
     *
     * @throws IllegalStateException if the order is not in APPROVED status
     */
    public void receive() {
        if (status != PurchaseOrderStatus.APPROVED) {
            throw new IllegalStateException("Can only receive approved purchase orders");
        }
        this.status = PurchaseOrderStatus.RECEIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels the purchase order.
     * Business rule: can only cancel pending or approved orders.
     *
     * @throws IllegalStateException if the order is already received or cancelled
     */
    public void cancel() {
        if (status == PurchaseOrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot cancel a received purchase order");
        }
        if (status == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("Purchase order is already cancelled");
        }
        this.status = PurchaseOrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the expected delivery date.
     *
     * @param newExpectedDeliveryDate the new expected delivery date (can be null)
     */
    public void updateExpectedDeliveryDate(LocalDate newExpectedDeliveryDate) {
        if (!Objects.equals(this.expectedDeliveryDate, newExpectedDeliveryDate)) {
            this.expectedDeliveryDate = newExpectedDeliveryDate;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the notes.
     *
     * @param newNotes the new notes (can be null)
     */
    public void updateNotes(String newNotes) {
        if (!Objects.equals(this.notes, newNotes)) {
            this.notes = newNotes;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if this purchase order is new (not yet persisted).
     *
     * @return true if the purchase order has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Checks if the purchase order can be modified.
     * Business rule: only pending and approved orders can be modified.
     *
     * @return true if the order can be modified, false otherwise
     */
    public boolean canBeModified() {
        return status == PurchaseOrderStatus.PENDING || status == PurchaseOrderStatus.APPROVED;
    }

    // Getters (no setters - controlled mutation through business methods)

    public PurchaseOrderId getId() {
        return id;
    }

    public PurchaseOrderNumber getOrderNumber() {
        return orderNumber;
    }

    public SupplierId getSupplierId() {
        return supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", orderNumber=" + orderNumber +
                ", supplierId=" + supplierId +
                ", orderDate=" + orderDate +
                ", expectedDeliveryDate=" + expectedDeliveryDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
