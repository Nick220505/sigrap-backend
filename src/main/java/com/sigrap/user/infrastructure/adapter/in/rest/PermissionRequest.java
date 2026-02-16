package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
    description = "Request body for creating or updating a permission",
    example = """
        {
            "name": "CREATE_PRODUCT",
            "resource": "PRODUCT",
            "action": "CREATE"
        }
        """
)
public record PermissionRequest(
    @Schema(
        description = "Permission name - must be unique",
        example = "CREATE_PRODUCT",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name cannot exceed 100 characters")
    String name,
    
    @Schema(
        description = "Resource this permission applies to (e.g., PRODUCT, USER, CATEGORY)",
        example = "PRODUCT",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    @NotBlank(message = "Resource is required")
    @Size(max = 50, message = "Resource cannot exceed 50 characters")
    String resource,
    
    @Schema(
        description = "Action this permission allows (e.g., CREATE, READ, UPDATE, DELETE)",
        example = "CREATE",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 50
    )
    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action cannot exceed 50 characters")
    String action
) {}
