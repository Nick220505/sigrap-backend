package com.sigrap.audit.application.service;

import com.sigrap.audit.application.port.in.CreateAuditLogUseCase;
import com.sigrap.audit.domain.event.*;
import com.sigrap.audit.domain.model.*;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Application service for creating audit log entries.
 * Implements the CreateAuditLogUseCase by converting domain events into audit logs.
 * 
 * <p>This service handles the orchestration of creating audit logs from various
 * domain events, mapping event types to audit actions and statuses.</p>
 */
@Service
@Transactional
public class CreateAuditLogService implements CreateAuditLogUseCase {
    
    private final AuditLogRepositoryPort auditLogRepository;
    
    public CreateAuditLogService(AuditLogRepositoryPort auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    @Override
    public AuditLog createFromEvent(DomainEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        
        AuditLog auditLog = mapEventToAuditLog(event);
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Maps a domain event to an audit log entry.
     * 
     * @param event The domain event
     * @return The audit log
     */
    private AuditLog mapEventToAuditLog(DomainEvent event) {
        return switch (event) {
            case EntityCreatedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.CREATE,
                    e.entityType(),
                    e.entityId(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    e.details(),
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case EntityUpdatedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.UPDATE,
                    e.entityType(),
                    e.entityId(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    e.changes(),
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case EntityDeletedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.DELETE,
                    e.entityType(),
                    e.entityId(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    e.details(),
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case BulkEntityDeletedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.BULK_DELETE,
                    e.entityType(),
                    String.join(",", e.entityIds()),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    "Deleted " + e.getDeletedCount() + " entities",
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case EntityViewedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.VIEW,
                    e.entityType(),
                    e.entityId(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    null,
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case UserLoginEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.LOGIN,
                    EntityType.USER,
                    e.performedBy(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    null,
                    AuditStatus.SUCCESS,
                    e.durationMs()
            );
            
            case UserLoginFailedEvent e -> createAuditLog(
                    e.username(),
                    AuditAction.LOGIN,
                    EntityType.USER,
                    e.username(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    "Login failed: " + e.reason(),
                    AuditStatus.ERROR,
                    e.durationMs()
            );
            
            case UserLogoutEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.LOGOUT,
                    EntityType.USER,
                    e.performedBy(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    null,
                    AuditStatus.SUCCESS,
                    null
            );
            
            case AccessDeniedEvent e -> createAuditLog(
                    e.performedBy(),
                    AuditAction.ACCESS_DENIED,
                    e.entityType() != null ? e.entityType() : EntityType.USER,
                    e.entityId(),
                    e.occurredAt(),
                    e.sourceIp(),
                    e.userAgent(),
                    "Access denied - Action: " + e.attemptedAction() + ", Reason: " + e.reason(),
                    AuditStatus.ERROR,
                    null
            );
            
            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.getClass().getName()
            );
        };
    }
    
    /**
     * Creates an audit log with the given parameters.
     */
    private AuditLog createAuditLog(
            String username,
            AuditAction action,
            EntityType entityType,
            String entityId,
            java.time.LocalDateTime timestamp,
            String sourceIp,
            String userAgent,
            String details,
            AuditStatus status,
            Long durationMs) {
        return new AuditLog(
                username,
                action,
                entityType,
                entityId,
                timestamp,
                sourceIp,
                userAgent,
                details,
                status,
                durationMs
        );
    }
}
