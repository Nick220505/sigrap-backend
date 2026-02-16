package com.sigrap.sale.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request payload for creating a sale return")
public record SaleReturnRequest(
    @Schema(description = "Unique return number", example = "RET-2024-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Return number is required")
    @Size(max = 50, message = "Return number cannot exceed 50 characters")
    String returnNumber,
    
    @Schema(description = "Original sale ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Sale ID is required")
    Long saleId,
    
    @Schema(description = "Date and time of the return", example = "2024-01-20T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Return date is required")
    LocalDateTime returnDate,
    
    @Schema(description = "Reason for the return", example = "Product defective", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 500)
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason,
    
    @Schema(description = "Refund amount", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Refund amount cannot be negative")
    BigDecimal refundAmount,
    
    @Schema(description = "Additional notes", example = "Customer provided receipt", maxLength = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
