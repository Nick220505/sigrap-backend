package com.sigrap.user.infrastructure.adapter.in.rest;

import java.util.Set;

/**
 * Response DTO for role operations.
 * Represents the role data returned to REST clients.
 *
 * @param id the role identifier
 * @param name the role name
 * @param description the role description
 * @param permissions the set of permission names assigned to the role
 */
public record RoleResponse(
    Long id,
    String name,
    String description,
    Set<String> permissions
) {}
