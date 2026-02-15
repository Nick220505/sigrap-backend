package com.sigrap.sale.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a sale transaction.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A sale represents a complete transaction where products are sold to a customer,
 * including payment information, items, and transaction status.</p>
 */
public class Sale {
    private final SaleId id;
    private final SaleNumber saleNumber;
    private final Long customerId;
    private final Long employeeId;
    private final LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<SaleItem> items;

    /**
     * Constructor for creating a new sale (without ID).
     * Used when creating a sale before persistence.
     *
     * @param saleNumber the sale number (required)
     * @param customerId the customer identifier (required)
     * @param employeeId the employee identifier (required)
     * @param saleDate the date of the sale (required)
     * @param paymentMethod the payment method used (required)
     * @param notes additional notes (optional)
     */
    public Sale(SaleNumber saleNumber, Long customerId, Long employeeId,
                LocalDateTime saleDate, PaymentMethod paymentMethod, String notes) {
        this(null, saleNumber, customerId, employeeId, saleDate, BigDecimal.ZERO,
             paymentMethod, SaleStatus.PENDING, notes, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a sale from persistence.
     *
     * @param id the sale identifier
     * @param saleNumber the sale number (required)
     * @param customerId the customer identifier (required)
     * @param employeeId the employee identifier (required)
     * @param saleDate the date of the sale (required)
     * @param totalAmount the total amount of the sale
     * @param paymentMethod the payment method used (required)
     * @param status the current status of the sale (required)
     * @param notes additional notes (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Sale(SaleId id, SaleNumber saleNumber, Long customerId, Long employeeId,
                LocalDateTime saleDate, BigDecimal totalAmount, PaymentMethod paymentMethod,
                SaleStatus status, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.saleNumber = Objects.requireNonNull(saleNumber, "Sale number cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.saleDate = Objects.requireNonNull(saleDate, "Sale date cannot be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.notes = notes;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        this.items = new ArrayList<>();
    }

    /**
     * Adds an item to this sale.
     * Automatically recalculates the total amount.
     *
     * @param item the sale item to add (required)
     */
    public void addItem(SaleItem item) {
        Objects.requireNonNull(item, "Sale item cannot be null");
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Cannot add items to a completed sale");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot add items to a cancelled sale");
        }
        items.add(item);
        calculateTotal();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Removes an item from this sale.
     * Automatically recalculates the total amount.
     *
     * @param item the sale item to remove (required)
     */
    public void removeItem(SaleItem item) {
        Objects.requireNonNull(item, "Sale item cannot be null");
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Cannot remove items from a completed sale");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot remove items from a cancelled sale");
        }
        items.remove(item);
        calculateTotal();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the total amount of the sale based on all items.
     * Updates the totalAmount field.
     */
    public void calculateTotal() {
        this.totalAmount = items.stream()
                .map(SaleItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Completes the sale, marking it as finalized.
     * A completed sale cannot be modified.
     */
    public void complete() {
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Sale is already completed");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled sale");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot complete a sale with no items");
        }
        this.status = SaleStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels the sale.
     * A cancelled sale cannot be modified or completed.
     */
    public void cancel() {
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed sale");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Sale is already cancelled");
        }
        this.status = SaleStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the payment method for this sale.
     *
     * @param newPaymentMethod the new payment method (required)
     */
    public void updatePaymentMethod(PaymentMethod newPaymentMethod) {
        Objects.requireNonNull(newPaymentMethod, "Payment method cannot be null");
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update payment method of a completed sale");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update payment method of a cancelled sale");
        }
        this.paymentMethod = newPaymentMethod;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the notes for this sale.
     *
     * @param newNotes the new notes (can be null)
     */
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if this sale is new (not yet persisted).
     *
     * @return true if the sale has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Checks if this sale can be modified.
     *
     * @return true if the sale is in PENDING status, false otherwise
     */
    public boolean canBeModified() {
        return status == SaleStatus.PENDING;
    }

    // Getters (no setters - controlled mutation through business methods)

    public SaleId getId() {
        return id;
    }

    public SaleNumber getSaleNumber() {
        return saleNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public SaleStatus getStatus() {
        return status;
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

    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return Objects.equals(id, sale.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", saleNumber=" + saleNumber +
                ", customerId=" + customerId +
                ", employeeId=" + employeeId +
                ", saleDate=" + saleDate +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", itemCount=" + items.size() +
                '}';
    }
}
