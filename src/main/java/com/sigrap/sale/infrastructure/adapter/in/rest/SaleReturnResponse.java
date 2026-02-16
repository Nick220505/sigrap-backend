package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.SaleReturnStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Sale return information returned in API responses")
public record SaleReturnResponse(
    @Schema(description = "Unique identifier of the sale return", example = "1")
    Long id,
    
    @Schema(description = "Unique return number", example = "RET-2024-001")
    String returnNumber,
    
    @Schema(description = "Original sale ID", example = "1")
    Long saleId,
    
    @Schema(description = "Date and time of the return", example = "2024-01-20T10:00:00")
    LocalDateTime returnDate,
    
    @Schema(description = "Reason for the return", example = "Product defective")
    String reason,
    
    @Schema(description = "Current status of the return", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED", "COMPLETED"})
    SaleReturnStatus status,
    
    @Schema(description = "Refund amount", example = "150.00")
    BigDecimal refundAmount,
    
    @Schema(description = "Additional notes", example = "Customer provided receipt")
    String notes,
    
    @Schema(description = "Timestamp when the return was created", example = "2024-01-20T10:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the return was last updated", example = "2024-01-20T11:00:00")
    LocalDateTime updatedAt
) {}
