package com.sigrap.sale.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a sale return transaction.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A sale return represents a transaction where products from a previous sale are returned,
 * including the reason for return, refund amount, and approval workflow.</p>
 */
public class SaleReturn {
    private final SaleReturnId id;
    private final SaleReturnNumber returnNumber;
    private final SaleId saleId;
    private final LocalDateTime returnDate;
    private final String reason;
    private SaleReturnStatus status;
    private BigDecimal refundAmount;
    private String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new sale return (without ID).
     * Used when creating a sale return before persistence.
     *
     * @param returnNumber the return number (required)
     * @param saleId the original sale identifier (required)
     * @param returnDate the date of the return (required)
     * @param reason the reason for the return (required)
     * @param refundAmount the refund amount (required)
     * @param notes additional notes (optional)
     */
    public SaleReturn(SaleReturnNumber returnNumber, SaleId saleId, LocalDateTime returnDate,
                     String reason, BigDecimal refundAmount, String notes) {
        this(null, returnNumber, saleId, returnDate, reason, SaleReturnStatus.PENDING,
             refundAmount, notes, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a sale return from persistence.
     *
     * @param id the sale return identifier
     * @param returnNumber the return number (required)
     * @param saleId the original sale identifier (required)
     * @param returnDate the date of the return (required)
     * @param reason the reason for the return (required)
     * @param status the current status of the return (required)
     * @param refundAmount the refund amount (required)
     * @param notes additional notes (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public SaleReturn(SaleReturnId id, SaleReturnNumber returnNumber, SaleId saleId,
                     LocalDateTime returnDate, String reason, SaleReturnStatus status,
                     BigDecimal refundAmount, String notes, LocalDateTime createdAt,
                     LocalDateTime updatedAt) {
        this.id = id;
        this.returnNumber = Objects.requireNonNull(returnNumber, "Return number cannot be null");
        this.saleId = Objects.requireNonNull(saleId, "Sale ID cannot be null");
        this.returnDate = Objects.requireNonNull(returnDate, "Return date cannot be null");
        
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be blank");
        }
        this.reason = reason;
        
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.refundAmount = Objects.requireNonNull(refundAmount, "Refund amount cannot be null");
        
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Refund amount cannot be negative");
        }
        
        this.notes = notes;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Approves the sale return.
     * Only pending returns can be approved.
     */
    public void approve() {
        if (status != SaleReturnStatus.PENDING) {
            throw new IllegalStateException("Only pending returns can be approved");
        }
        this.status = SaleReturnStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rejects the sale return.
     * Only pending returns can be rejected.
     */
    public void reject() {
        if (status != SaleReturnStatus.PENDING) {
            throw new IllegalStateException("Only pending returns can be rejected");
        }
        this.status = SaleReturnStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Completes the sale return, marking the refund as processed.
     * Only approved returns can be completed.
     */
    public void complete() {
        if (status != SaleReturnStatus.APPROVED) {
            throw new IllegalStateException("Only approved returns can be completed");
        }
        this.status = SaleReturnStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the refund amount for this return.
     * Can only be updated while in PENDING status.
     *
     * @param newRefundAmount the new refund amount (must be non-negative)
     */
    public void updateRefundAmount(BigDecimal newRefundAmount) {
        Objects.requireNonNull(newRefundAmount, "Refund amount cannot be null");
        if (newRefundAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Refund amount cannot be negative");
        }
        if (status != SaleReturnStatus.PENDING) {
            throw new IllegalStateException("Can only update refund amount for pending returns");
        }
        this.refundAmount = newRefundAmount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the notes for this return.
     *
     * @param newNotes the new notes (can be null)
     */
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if this sale return is new (not yet persisted).
     *
     * @return true if the return has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Checks if this return can be modified.
     *
     * @return true if the return is in PENDING status, false otherwise
     */
    public boolean canBeModified() {
        return status == SaleReturnStatus.PENDING;
    }

    /**
     * Checks if this return is finalized (completed or rejected).
     *
     * @return true if the return is completed or rejected, false otherwise
     */
    public boolean isFinalized() {
        return status == SaleReturnStatus.COMPLETED || status == SaleReturnStatus.REJECTED;
    }

    // Getters (no setters - controlled mutation through business methods)

    public SaleReturnId getId() {
        return id;
    }

    public SaleReturnNumber getReturnNumber() {
        return returnNumber;
    }

    public SaleId getSaleId() {
        return saleId;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public String getReason() {
        return reason;
    }

    public SaleReturnStatus getStatus() {
        return status;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
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
        SaleReturn that = (SaleReturn) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SaleReturn{" +
                "id=" + id +
                ", returnNumber=" + returnNumber +
                ", saleId=" + saleId +
                ", returnDate=" + returnDate +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", refundAmount=" + refundAmount +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
