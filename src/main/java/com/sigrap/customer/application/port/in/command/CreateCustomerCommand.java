package com.sigrap.customer.application.port.in.command;

/**
 * Command for creating a new customer.
 * This is an immutable data carrier that represents the user's intent to create a customer.
 *
 * @param fullName the full name of the customer to create
 * @param documentId the document ID of the customer (optional)
 * @param email the email address of the customer
 * @param phoneNumber the phone number of the customer (optional)
 * @param address the physical address of the customer (optional)
 */
public record CreateCustomerCommand(
    String fullName,
    String documentId,
    String email,
    String phoneNumber,
    String address
) {}
