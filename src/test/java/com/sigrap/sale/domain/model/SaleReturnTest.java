package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SaleReturnTest {

    @Test
    void shouldCreateSaleReturnWithValidData() {
        SaleReturnNumber returnNumber = new SaleReturnNumber("RET-001");
        SaleId saleId = new SaleId(1L);
        LocalDateTime returnDate = LocalDateTime.now();
        String reason = "Defective product";
        BigDecimal refundAmount = new BigDecimal("50.00");

        SaleReturn saleReturn = new SaleReturn(returnNumber, saleId, returnDate, reason, refundAmount, "Notes");

        assertNull(saleReturn.getId());
        assertEquals(returnNumber, saleReturn.getReturnNumber());
        assertEquals(saleId, saleReturn.getSaleId());
        assertEquals(returnDate, saleReturn.getReturnDate());
        assertEquals(reason, saleReturn.getReason());
        assertEquals(SaleReturnStatus.PENDING, saleReturn.getStatus());
        assertEquals(refundAmount, saleReturn.getRefundAmount());
        assertEquals("Notes", saleReturn.getNotes());
    }

    @Test
    void shouldCreateSaleReturnWithFullConstructor() {
        SaleReturnId id = new SaleReturnId(1L);
        SaleReturnNumber returnNumber = new SaleReturnNumber("RET-001");
        SaleId saleId = new SaleId(1L);
        LocalDateTime returnDate = LocalDateTime.now();
        String reason = "Wrong item";
        SaleReturnStatus status = SaleReturnStatus.APPROVED;
        BigDecimal refundAmount = new BigDecimal("75.00");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        SaleReturn saleReturn = new SaleReturn(id, returnNumber, saleId, returnDate, reason,
                status, refundAmount, "Notes", createdAt, updatedAt);

        assertEquals(id, saleReturn.getId());
        assertEquals(status, saleReturn.getStatus());
        assertEquals(refundAmount, saleReturn.getRefundAmount());
    }

    @Test
    void shouldThrowExceptionWhenReturnNumberIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleReturn(null, new SaleId(1L), LocalDateTime.now(), "Reason",
                        new BigDecimal("50.00"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSaleIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), null, LocalDateTime.now(),
                        "Reason", new BigDecimal("50.00"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnDateIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), new SaleId(1L), null,
                        "Reason", new BigDecimal("50.00"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenReasonIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), new SaleId(1L),
                        LocalDateTime.now(), null, new BigDecimal("50.00"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenReasonIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), new SaleId(1L),
                        LocalDateTime.now(), "   ", new BigDecimal("50.00"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRefundAmountIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), new SaleId(1L),
                        LocalDateTime.now(), "Reason", null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRefundAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleReturn(new SaleReturnNumber("RET-001"), new SaleId(1L),
                        LocalDateTime.now(), "Reason", new BigDecimal("-10.00"), null)
        );
    }

    @Test
    void shouldAcceptZeroRefundAmount() {
        SaleReturn saleReturn = new SaleReturn(new SaleReturnNumber("RET-001"),
                new SaleId(1L), LocalDateTime.now(), "Reason", BigDecimal.ZERO, null);
        assertEquals(BigDecimal.ZERO, saleReturn.getRefundAmount());
    }

    @Test
    void shouldApprovePendingReturn() {
        SaleReturn saleReturn = createTestSaleReturn();

        saleReturn.approve();

        assertEquals(SaleReturnStatus.APPROVED, saleReturn.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonPendingReturn() {
        SaleReturn saleReturn = createApprovedSaleReturn();
        assertThrows(IllegalStateException.class, () -> saleReturn.approve());
    }

    @Test
    void shouldRejectPendingReturn() {
        SaleReturn saleReturn = createTestSaleReturn();

        saleReturn.reject();

        assertEquals(SaleReturnStatus.REJECTED, saleReturn.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonPendingReturn() {
        SaleReturn saleReturn = createApprovedSaleReturn();
        assertThrows(IllegalStateException.class, () -> saleReturn.reject());
    }

    @Test
    void shouldCompleteApprovedReturn() {
        SaleReturn saleReturn = createApprovedSaleReturn();

        saleReturn.complete();

        assertEquals(SaleReturnStatus.COMPLETED, saleReturn.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCompletingNonApprovedReturn() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertThrows(IllegalStateException.class, () -> saleReturn.complete());
    }

    @Test
    void shouldThrowExceptionWhenCompletingRejectedReturn() {
        SaleReturn saleReturn = createRejectedSaleReturn();
        assertThrows(IllegalStateException.class, () -> saleReturn.complete());
    }

    @Test
    void shouldUpdateRefundAmountForPendingReturn() {
        SaleReturn saleReturn = createTestSaleReturn();

        saleReturn.updateRefundAmount(new BigDecimal("75.00"));

        assertEquals(new BigDecimal("75.00"), saleReturn.getRefundAmount());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRefundAmountToNull() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertThrows(NullPointerException.class, () -> saleReturn.updateRefundAmount(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRefundAmountToNegative() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertThrows(IllegalArgumentException.class, () ->
                saleReturn.updateRefundAmount(new BigDecimal("-10.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRefundAmountOfNonPendingReturn() {
        SaleReturn saleReturn = createApprovedSaleReturn();
        assertThrows(IllegalStateException.class, () ->
                saleReturn.updateRefundAmount(new BigDecimal("100.00"))
        );
    }

    @Test
    void shouldUpdateNotes() {
        SaleReturn saleReturn = createTestSaleReturn();

        saleReturn.updateNotes("Updated notes");

        assertEquals("Updated notes", saleReturn.getNotes());
    }

    @Test
    void shouldAllowNullNotes() {
        SaleReturn saleReturn = createTestSaleReturn();
        saleReturn.updateNotes(null);
        assertNull(saleReturn.getNotes());
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertTrue(saleReturn.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        SaleReturn saleReturn = new SaleReturn(
                new SaleReturnId(1L),
                new SaleReturnNumber("RET-001"),
                new SaleId(1L),
                LocalDateTime.now(),
                "Reason",
                SaleReturnStatus.PENDING,
                new BigDecimal("50.00"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertFalse(saleReturn.isNew());
    }

    @Test
    void shouldReturnTrueForCanBeModifiedWhenStatusIsPending() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertTrue(saleReturn.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenStatusIsApproved() {
        SaleReturn saleReturn = createApprovedSaleReturn();
        assertFalse(saleReturn.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenStatusIsRejected() {
        SaleReturn saleReturn = createRejectedSaleReturn();
        assertFalse(saleReturn.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenStatusIsCompleted() {
        SaleReturn saleReturn = createCompletedSaleReturn();
        assertFalse(saleReturn.canBeModified());
    }

    @Test
    void shouldReturnFalseForIsFinalizedWhenStatusIsPending() {
        SaleReturn saleReturn = createTestSaleReturn();
        assertFalse(saleReturn.isFinalized());
    }

    @Test
    void shouldReturnFalseForIsFinalizedWhenStatusIsApproved() {
        SaleReturn saleReturn = createApprovedSaleReturn();
        assertFalse(saleReturn.isFinalized());
    }

    @Test
    void shouldReturnTrueForIsFinalizedWhenStatusIsCompleted() {
        SaleReturn saleReturn = createCompletedSaleReturn();
        assertTrue(saleReturn.isFinalized());
    }

    @Test
    void shouldReturnTrueForIsFinalizedWhenStatusIsRejected() {
        SaleReturn saleReturn = createRejectedSaleReturn();
        assertTrue(saleReturn.isFinalized());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        SaleReturnId id = new SaleReturnId(1L);
        SaleReturn saleReturn1 = new SaleReturn(id, new SaleReturnNumber("RET-001"),
                new SaleId(1L), LocalDateTime.now(), "Reason", SaleReturnStatus.PENDING,
                new BigDecimal("50.00"), null, LocalDateTime.now(), LocalDateTime.now());
        SaleReturn saleReturn2 = new SaleReturn(id, new SaleReturnNumber("RET-002"),
                new SaleId(1L), LocalDateTime.now(), "Reason", SaleReturnStatus.PENDING,
                new BigDecimal("50.00"), null, LocalDateTime.now(), LocalDateTime.now());
        assertEquals(saleReturn1, saleReturn2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        SaleReturn saleReturn1 = new SaleReturn(new SaleReturnId(1L),
                new SaleReturnNumber("RET-001"), new SaleId(1L), LocalDateTime.now(),
                "Reason", SaleReturnStatus.PENDING, new BigDecimal("50.00"), null,
                LocalDateTime.now(), LocalDateTime.now());
        SaleReturn saleReturn2 = new SaleReturn(new SaleReturnId(2L),
                new SaleReturnNumber("RET-001"), new SaleId(1L), LocalDateTime.now(),
                "Reason", SaleReturnStatus.PENDING, new BigDecimal("50.00"), null,
                LocalDateTime.now(), LocalDateTime.now());
        assertNotEquals(saleReturn1, saleReturn2);
    }

    @Test
    void shouldHaveValidToString() {
        SaleReturn saleReturn = createTestSaleReturn();
        String toString = saleReturn.toString();
        assertTrue(toString.contains("SaleReturn"));
        assertTrue(toString.contains("returnNumber="));
        assertTrue(toString.contains("status="));
    }

    // Helper methods

    private SaleReturn createTestSaleReturn() {
        return new SaleReturn(
                new SaleReturnNumber("RET-001"),
                new SaleId(1L),
                LocalDateTime.now(),
                "Defective product",
                new BigDecimal("50.00"),
                null
        );
    }

    private SaleReturn createApprovedSaleReturn() {
        SaleReturn saleReturn = createTestSaleReturn();
        saleReturn.approve();
        return saleReturn;
    }

    private SaleReturn createRejectedSaleReturn() {
        SaleReturn saleReturn = createTestSaleReturn();
        saleReturn.reject();
        return saleReturn;
    }

    private SaleReturn createCompletedSaleReturn() {
        SaleReturn saleReturn = createTestSaleReturn();
        saleReturn.approve();
        saleReturn.complete();
        return saleReturn;
    }
}
