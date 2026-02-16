package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Request payload for creating a sale")
public record SaleRequest(
    @Schema(description = "Unique sale number", example = "SALE-2024-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Sale number is required")
    @Size(max = 50, message = "Sale number cannot exceed 50 characters")
    String saleNumber,
    
    @Schema(description = "Customer ID for this sale", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @Schema(description = "Employee ID who processed the sale", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Employee ID is required")
    Long employeeId,
    
    @Schema(description = "Date and time of the sale", example = "2024-01-15T14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Sale date is required")
    LocalDateTime saleDate,
    
    @Schema(description = "Payment method used", example = "CASH", requiredMode = Schema.RequiredMode.REQUIRED, 
        allowableValues = {"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"})
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    @Schema(description = "Additional notes", example = "Customer requested gift wrapping", maxLength = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
