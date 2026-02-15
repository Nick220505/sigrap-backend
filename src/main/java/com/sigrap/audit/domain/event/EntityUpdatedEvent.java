package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;

/**
 * Domain event representing the update of an entity.
 * This event is published when an existing entity is modified.
 */
public record EntityUpdatedEvent(
        EntityType entityType,
        String entityId,
        String performedBy,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        String changes,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new entity updated event.
     * 
     * @param entityType The type of entity that was updated
     * @param entityId The ID of the updated entity
     * @param performedBy The username who updated the entity
     * @param occurredAt When the update occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param changes Description of what changed (may be null)
     * @param durationMs The duration of the operation in milliseconds (may be null)
     */
    public EntityUpdatedEvent {
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
