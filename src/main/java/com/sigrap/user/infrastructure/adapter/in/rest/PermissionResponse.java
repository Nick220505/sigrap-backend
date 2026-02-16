package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for permission operations.
 * Represents the permission data returned to REST clients.
 *
 * @param id the permission identifier
 * @param name the permission name
 * @param resource the resource this permission applies to
 * @param action the action this permission allows
 */
@Schema(description = "Permission response with all details")
public record PermissionResponse(
    @Schema(description = "Unique identifier", example = "1")
    Long id,
    
    @Schema(description = "Permission name", example = "CREATE_PRODUCT")
    String name,
    
    @Schema(description = "Resource this permission applies to", example = "PRODUCT")
    String resource,
    
    @Schema(description = "Action this permission allows", example = "CREATE")
    String action
) {}
