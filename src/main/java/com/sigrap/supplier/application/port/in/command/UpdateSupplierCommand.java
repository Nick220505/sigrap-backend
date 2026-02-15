package com.sigrap.supplier.application.port.in.command;

/**
 * Command for updating an existing supplier.
 * This is an immutable data carrier that represents the user's intent to update a supplier.
 *
 * @param name the updated name of the supplier
 * @param contactName the updated contact person's name (optional)
 * @param email the updated supplier's email address
 * @param phone the updated supplier's phone number (optional)
 * @param address the updated supplier's physical address (optional)
 */
public record UpdateSupplierCommand(
    String name,
    String contactName,
    String email,
    String phone,
    String address
) {}
