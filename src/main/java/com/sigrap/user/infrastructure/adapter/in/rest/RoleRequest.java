package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for role operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the role name (required, max 50 characters)
 * @param description the role description (optional, max 500 characters)
 */
@Schema(
    description = "Request body for creating or updating a role",
    example = """
        {
            "name": "MANAGER",
            "description": "Manager role with access to employee and inventory management"
        }
        """
)
public record RoleRequest(
    @Schema(
        description = "Role name - must be unique across the system",
        example = "MANAGER",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    String name,
    
    @Schema(
        description = "Optional detailed description of the role and its permissions",
        example = "Manager role with access to employee and inventory management",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 500
    )
    @Size(max = 500, message = "Role description cannot exceed 500 characters")
    String description
) {}
