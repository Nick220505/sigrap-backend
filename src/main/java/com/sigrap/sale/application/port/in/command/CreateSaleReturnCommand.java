package com.sigrap.sale.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Command for creating a new sale return.
 * This is an immutable data carrier that represents the user's intent to create a sale return.
 *
 * @param returnNumber the return number
 * @param saleId the original sale identifier
 * @param returnDate the date of the return
 * @param reason the reason for the return
 * @param refundAmount the refund amount
 * @param notes additional notes (optional)
 */
public record CreateSaleReturnCommand(
    String returnNumber,
    Long saleId,
    LocalDateTime returnDate,
    String reason,
    BigDecimal refundAmount,
    String notes
) {}
