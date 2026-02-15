package com.sigrap.audit.infrastructure.adapter.in.event;

import com.sigrap.audit.application.port.in.CreateAuditLogUseCase;
import com.sigrap.audit.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Event listener adapter that listens to all domain events and creates audit logs.
 * 
 * <p>This is an input adapter that translates Spring events into use case calls.
 * It listens to all {@link DomainEvent} instances published through Spring's
 * event mechanism and delegates to the {@link CreateAuditLogUseCase} to create
 * audit log entries.</p>
 * 
 * <p>The listener is asynchronous to avoid blocking the main transaction and
 * uses a new transaction to ensure audit logs are persisted even if the main
 * transaction fails.</p>
 */
@Component
@Slf4j
public class DomainEventListener {
    
    private final CreateAuditLogUseCase createAuditLogUseCase;
    
    public DomainEventListener(CreateAuditLogUseCase createAuditLogUseCase) {
        this.createAuditLogUseCase = createAuditLogUseCase;
    }
    
    /**
     * Handles domain events by creating audit log entries.
     * 
     * <p>This method is executed asynchronously in a separate transaction to ensure:
     * <ul>
     *   <li>The main business transaction is not blocked</li>
     *   <li>Audit logs are persisted even if the main transaction fails</li>
     *   <li>Failures in audit logging don't affect the main business operation</li>
     * </ul>
     * 
     * @param event The domain event to audit
     */
    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleDomainEvent(DomainEvent event) {
        try {
            log.debug("Received domain event: {}", event.getClass().getSimpleName());
            createAuditLogUseCase.createFromEvent(event);
            log.debug("Audit log created for event: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            // Log the error but don't propagate it to avoid affecting the main transaction
            log.error("Failed to create audit log for event: {}", event.getClass().getSimpleName(), e);
        }
    }
}
