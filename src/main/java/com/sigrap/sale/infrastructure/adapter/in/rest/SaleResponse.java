package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import com.sigrap.sale.domain.model.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(
    description = "Sale information returned in API responses",
    example = """
        {
            "id": 1,
            "saleNumber": "SALE-2024-001",
            "customerId": 1,
            "employeeId": 1,
            "saleDate": "2024-01-15T14:30:00",
            "totalAmount": 150.00,
            "paymentMethod": "CASH",
            "status": "COMPLETED",
            "notes": "Customer requested gift wrapping",
            "createdAt": "2024-01-15T14:30:00",
            "updatedAt": "2024-01-15T15:00:00"
        }
        """
)
public record SaleResponse(
    @Schema(description = "Unique identifier of the sale", example = "1")
    Long id,
    
    @Schema(description = "Unique sale number", example = "SALE-2024-001")
    String saleNumber,
    
    @Schema(description = "Customer ID for this sale", example = "1")
    Long customerId,
    
    @Schema(description = "Employee ID who processed the sale", example = "1")
    Long employeeId,
    
    @Schema(description = "Date and time of the sale", example = "2024-01-15T14:30:00")
    LocalDateTime saleDate,
    
    @Schema(description = "Total amount of the sale", example = "150.00")
    BigDecimal totalAmount,
    
    @Schema(description = "Payment method used", example = "CASH", allowableValues = {"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"})
    PaymentMethod paymentMethod,
    
    @Schema(description = "Current status of the sale", example = "COMPLETED", allowableValues = {"PENDING", "COMPLETED", "CANCELLED"})
    SaleStatus status,
    
    @Schema(description = "Additional notes", example = "Customer requested gift wrapping")
    String notes,
    
    @Schema(description = "Timestamp when the sale was created", example = "2024-01-15T14:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the sale was last updated", example = "2024-01-15T15:00:00")
    LocalDateTime updatedAt
) {}
