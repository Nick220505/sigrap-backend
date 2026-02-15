package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;

/**
 * Domain event representing an access denied situation.
 * This event is published when a user attempts to access a resource
 * they don't have permission for.
 * Important for security monitoring and detecting unauthorized access attempts.
 */
public record AccessDeniedEvent(
        String username,
        EntityType entityType,
        String entityId,
        String attemptedAction,
        String reason,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent
) implements DomainEvent {
    
    /**
     * Creates a new access denied event.
     * 
     * @param username The username who was denied access
     * @param entityType The type of entity they tried to access (may be null)
     * @param entityId The ID of the entity they tried to access (may be null)
     * @param attemptedAction The action they tried to perform
     * @param reason The reason access was denied
     * @param occurredAt When the access denial occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     */
    public AccessDeniedEvent {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (attemptedAction == null || attemptedAction.isBlank()) {
            throw new IllegalArgumentException("Attempted action cannot be blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String performedBy() {
        return username;
    }
}
