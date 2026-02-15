package com.sigrap.audit.infrastructure.adapter.out.event;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Spring-based implementation of the EventPublisherPort.
 * 
 * <p>This output adapter uses Spring's {@link ApplicationEventPublisher} to publish
 * domain events to the Spring event bus. Other components can listen to these events
 * using {@link org.springframework.context.event.EventListener}.</p>
 * 
 * <p>This adapter isolates the domain and application layers from Spring's event
 * mechanism, allowing the core business logic to remain framework-agnostic.</p>
 */
@Component
@Slf4j
public class SpringEventPublisherAdapter implements EventPublisherPort {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SpringEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    /**
     * Publishes a domain event using Spring's event mechanism.
     * 
     * @param event The domain event to publish
     * @throws IllegalArgumentException if event is null
     */
    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        log.debug("Publishing domain event: {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
        log.trace("Domain event published: {}", event);
    }
}
