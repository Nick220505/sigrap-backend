package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain event representing the bulk deletion of entities.
 * This event is published when multiple entities are deleted in a single operation.
 */
public record BulkEntityDeletedEvent(
        EntityType entityType,
        List<String> entityIds,
        String performedBy,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new bulk entity deleted event.
     * 
     * @param entityType The type of entities that were deleted
     * @param entityIds The IDs of the deleted entities
     * @param performedBy The username who deleted the entities
     * @param occurredAt When the deletion occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param durationMs The duration of the operation in milliseconds (may be null)
     */
    public BulkEntityDeletedEvent {
        if (entityType == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }
        if (entityIds == null || entityIds.isEmpty()) {
            throw new IllegalArgumentException("Entity IDs cannot be empty");
        }
        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException("Performed by cannot be blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    /**
     * Gets the count of entities deleted.
     * 
     * @return The number of entities deleted
     */
    public int getDeletedCount() {
        return entityIds.size();
    }
}
