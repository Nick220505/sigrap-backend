package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
    description = "Role response with all details including assigned permissions",
    example = """
        {
            "id": 1,
            "name": "MANAGER",
            "description": "Manager role with access to employee and inventory management",
            "permissions": ["READ_PRODUCTS", "WRITE_PRODUCTS", "READ_EMPLOYEES"]
        }
        """
)
public record RoleResponse(
    @Schema(description = "Unique identifier", example = "1")
    Long id,
    
    @Schema(description = "Role name", example = "MANAGER")
    String name,
    
    @Schema(description = "Detailed description of the role and its purpose", example = "Manager role with access to employee and inventory management")
    String description,
    
    @Schema(description = "Set of permission names assigned to this role", example = "[\"READ_PRODUCTS\", \"WRITE_PRODUCTS\", \"READ_EMPLOYEES\"]")
    Set<String> permissions
) {}
