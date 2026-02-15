package com.sigrap.user.infrastructure.adapter.in.rest;

/**
 * Response DTO for permission operations.
 * Represents the permission data returned to REST clients.
 *
 * @param id the permission identifier
 * @param name the permission name
 * @param resource the resource this permission applies to
 * @param action the action this permission allows
 */
public record PermissionResponse(
    Long id,
    String name,
    String resource,
    String action
) {}
