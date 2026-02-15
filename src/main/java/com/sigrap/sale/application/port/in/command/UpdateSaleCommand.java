package com.sigrap.sale.application.port.in.command;

import com.sigrap.sale.domain.model.PaymentMethod;

/**
 * Command for updating an existing sale.
 * This is an immutable data carrier that represents the user's intent to update a sale.
 *
 * @param paymentMethod the payment method used (optional)
 * @param notes additional notes (optional)
 */
public record UpdateSaleCommand(
    PaymentMethod paymentMethod,
    String notes
) {}
