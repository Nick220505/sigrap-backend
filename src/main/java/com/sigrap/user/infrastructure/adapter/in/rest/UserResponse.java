package com.sigrap.user.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for user operations.
 * Represents the user data returned to REST clients.
 *
 * @param id the user identifier
 * @param username the username
 * @param email the email address
 * @param enabled whether the user account is enabled
 * @param roles the set of role names assigned to the user
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record UserResponse(
    Long id,
    String username,
    String email,
    boolean enabled,
    Set<String> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
