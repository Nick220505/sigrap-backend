package com.sigrap.user.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
    description = "User response with all details including assigned roles",
    example = """
        {
            "id": 1,
            "username": "johndoe",
            "email": "johndoe@example.com",
            "enabled": true,
            "roles": ["ADMIN", "USER"],
            "createdAt": "2026-02-15T21:00:00",
            "updatedAt": "2026-02-15T21:00:00"
        }
        """
)
public record UserResponse(
    @Schema(description = "Unique identifier", example = "1")
    Long id,
    
    @Schema(description = "Username", example = "johndoe")
    String username,
    
    @Schema(description = "Email address", example = "johndoe@example.com")
    String email,
    
    @Schema(description = "Whether the user account is enabled and can authenticate", example = "true")
    boolean enabled,
    
    @Schema(description = "Set of role names assigned to the user", example = "[\"ADMIN\", \"USER\"]")
    Set<String> roles,
    
    @Schema(description = "Timestamp when the user was created (ISO 8601 format)", example = "2026-02-15T21:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the user was last updated (ISO 8601 format)", example = "2026-02-15T21:00:00")
    LocalDateTime updatedAt
) {}
