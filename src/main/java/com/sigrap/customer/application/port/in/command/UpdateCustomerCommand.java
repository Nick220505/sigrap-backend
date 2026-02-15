package com.sigrap.customer.application.port.in.command;

/**
 * Command for updating an existing customer.
 * This is an immutable data carrier that represents the user's intent to update a customer.
 *
 * @param fullName the new full name of the customer
 * @param documentId the new document ID of the customer (optional)
 * @param email the new email address of the customer
 * @param phoneNumber the new phone number of the customer (optional)
 * @param address the new physical address of the customer (optional)
 */
public record UpdateCustomerCommand(
    String fullName,
    String documentId,
    String email,
    String phoneNumber,
    String address
) {}
