package com.sigrap.sale.application.port.in.command;

import com.sigrap.sale.domain.model.PaymentMethod;

import java.time.LocalDateTime;

/**
 * Command for creating a new sale.
 * This is an immutable data carrier that represents the user's intent to create a sale.
 *
 * @param saleNumber the sale number
 * @param customerId the customer identifier
 * @param employeeId the employee identifier
 * @param saleDate the date of the sale
 * @param paymentMethod the payment method used
 * @param notes additional notes (optional)
 */
public record CreateSaleCommand(
    String saleNumber,
    Long customerId,
    Long employeeId,
    LocalDateTime saleDate,
    PaymentMethod paymentMethod,
    String notes
) {}
