package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import com.sigrap.sale.domain.model.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for sale operations.
 * Represents the sale data returned to REST clients.
 *
 * @param id the sale identifier
 * @param saleNumber the sale number
 * @param customerId the customer identifier
 * @param employeeId the employee identifier
 * @param saleDate the date of the sale
 * @param totalAmount the total amount of the sale
 * @param paymentMethod the payment method used
 * @param status the current status of the sale
 * @param notes additional notes
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record SaleResponse(
    Long id,
    String saleNumber,
    Long customerId,
    Long employeeId,
    LocalDateTime saleDate,
    BigDecimal totalAmount,
    PaymentMethod paymentMethod,
    SaleStatus status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
