package com.sigrap.supplier.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

/**
 * Response DTO for supplier operations.
 * Represents the supplier data returned to REST clients.
 *
 * @param id the supplier identifier
 * @param name the supplier name
 * @param contactName the contact person's name
 * @param email the supplier email
 * @param phone the supplier phone number
 * @param address the supplier address
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record SupplierResponse(
    Long id,
    String name,
    String contactName,
    String email,
    String phone,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
