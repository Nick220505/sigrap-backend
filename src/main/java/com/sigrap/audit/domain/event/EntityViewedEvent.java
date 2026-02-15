package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;

/**
 * Domain event representing the viewing of an entity.
 * This event is published when an entity is accessed or viewed.
 * Useful for tracking access to sensitive information.
 */
public record EntityViewedEvent(
        EntityType entityType,
        String entityId,
        String performedBy,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new entity viewed event.
     * 
     * @param entityType The type of entity that was viewed
     * @param entityId The ID of the viewed entity
     * @param performedBy The username who viewed the entity
     * @param occurredAt When the view occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param durationMs The duration of the operation in milliseconds (may be null)
     */
    public EntityViewedEvent {
        if (entityType == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }
        if (entityId == null || entityId.isBlank()) {
            throw new IllegalArgumentException("Entity ID cannot be blank");
        }
        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException("Performed by cannot be blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
}
