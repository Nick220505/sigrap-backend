package com.sigrap.supplier.application.port.in.command;

/**
 * Command for creating a new supplier.
 * This is an immutable data carrier that represents the user's intent to create a supplier.
 *
 * @param name the name of the supplier to create
 * @param contactName the contact person's name (optional)
 * @param email the supplier's email address
 * @param phone the supplier's phone number (optional)
 * @param address the supplier's physical address (optional)
 */
public record CreateSupplierCommand(
    String name,
    String contactName,
    String email,
    String phone,
    String address
) {}
