package com.sigrap.audit.domain.event;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events in the system.
 * Domain events represent significant occurrences in the domain that other parts
 * of the system may be interested in.
 * 
 * <p>All domain events should be immutable and contain all information needed
 * to process the event.</p>
 */
public interface DomainEvent {
    
    /**
     * Gets the timestamp when the event occurred.
     * 
     * @return The event timestamp
     */
    LocalDateTime occurredAt();
    
    /**
     * Gets the username of the user who triggered the event.
     * 
     * @return The username, or "system" for system-generated events
     */
    String performedBy();
}
