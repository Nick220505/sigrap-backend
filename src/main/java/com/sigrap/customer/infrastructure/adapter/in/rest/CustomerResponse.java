package com.sigrap.customer.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

/**
 * Response DTO for customer operations.
 * Represents the customer data returned to REST clients.
 *
 * @param id the customer identifier
 * @param fullName the customer's full name
 * @param documentId the customer's document ID
 * @param email the customer's email address
 * @param phoneNumber the customer's phone number
 * @param address the customer's physical address
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record CustomerResponse(
    Long id,
    String fullName,
    String documentId,
    String email,
    String phoneNumber,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
