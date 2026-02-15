package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;

/**
 * Domain event representing the deletion of an entity.
 * This event is published when an entity is deleted from the system.
 */
public record EntityDeletedEvent(
        EntityType entityType,
        String entityId,
        String performedBy,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        String details,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new entity deleted event.
     * 
     * @param entityType The type of entity that was deleted
     * @param entityId The ID of the deleted entity
     * @param performedBy The username who deleted the entity
     * @param occurredAt When the deletion occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param details Additional details about the deletion (may be null)
     * @param durationMs The duration of the operation in milliseconds (may be null)
     */
    public EntityDeletedEvent {
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
