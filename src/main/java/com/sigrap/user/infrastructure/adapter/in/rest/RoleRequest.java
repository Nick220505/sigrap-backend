package com.sigrap.user.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for role operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the role name (required, max 50 characters)
 * @param description the role description (optional, max 500 characters)
 */
public record RoleRequest(
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    String name,
    
    @Size(max = 500, message = "Role description cannot exceed 500 characters")
    String description
) {}
