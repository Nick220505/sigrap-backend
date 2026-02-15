package com.sigrap.audit.application.port.in;

import com.sigrap.audit.domain.event.DomainEvent;
import com.sigrap.audit.domain.model.AuditLog;

/**
 * Use case for creating audit log entries.
 * This interface defines the contract for creating audit logs from domain events.
 */
public interface CreateAuditLogUseCase {
    
    /**
     * Creates an audit log entry from a domain event.
     * 
     * @param event The domain event to log
     * @return The created audit log
     * @throws IllegalArgumentException if event is null
     */
    AuditLog createFromEvent(DomainEvent event);
}
