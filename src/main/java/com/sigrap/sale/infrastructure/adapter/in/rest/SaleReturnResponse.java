package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.SaleReturnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for sale return operations.
 * Represents the sale return data returned to REST clients.
 *
 * @param id the sale return identifier
 * @param returnNumber the return number
 * @param saleId the original sale identifier
 * @param returnDate the date of the return
 * @param reason the reason for the return
 * @param status the current status of the return
 * @param refundAmount the refund amount
 * @param notes additional notes
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record SaleReturnResponse(
    Long id,
    String returnNumber,
    Long saleId,
    LocalDateTime returnDate,
    String reason,
    SaleReturnStatus status,
    BigDecimal refundAmount,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
