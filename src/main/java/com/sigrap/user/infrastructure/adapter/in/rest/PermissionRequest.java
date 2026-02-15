package com.sigrap.user.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for permission operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the permission name (required, max 100 characters)
 * @param resource the resource this permission applies to (required, max 50 characters)
 * @param action the action this permission allows (required, max 50 characters)
 */
public record PermissionRequest(
    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name cannot exceed 100 characters")
    String name,
    
    @NotBlank(message = "Resource is required")
    @Size(max = 50, message = "Resource cannot exceed 50 characters")
    String resource,
    
    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action cannot exceed 50 characters")
    String action
) {}
