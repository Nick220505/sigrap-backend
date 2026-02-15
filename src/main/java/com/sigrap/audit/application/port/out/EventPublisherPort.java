package com.sigrap.audit.application.port.out;

import com.sigrap.audit.domain.event.DomainEvent;

/**
 * Output port for publishing domain events.
 * This interface defines the contract for publishing events to the event bus.
 * 
 * <p>Implementations of this port will handle the actual event publishing mechanism,
 * such as Spring's ApplicationEventPublisher or a message broker.</p>
 * 
 * <p>This port is used by application services to publish domain events that
 * other parts of the system (like the audit module) can listen to and process.</p>
 */
public interface EventPublisherPort {
    
    /**
     * Publishes a domain event to the event bus.
     * 
     * @param event The domain event to publish
     * @throws IllegalArgumentException if event is null
     */
    void publish(DomainEvent event);
}
