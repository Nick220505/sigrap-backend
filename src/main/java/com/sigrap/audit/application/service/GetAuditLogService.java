package com.sigrap.audit.application.service;

import com.sigrap.audit.application.port.in.GetAuditLogUseCase;
import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Application service for retrieving audit log entries.
 * Implements the GetAuditLogUseCase by delegating to the repository port.
 * 
 * <p>This service handles the orchestration of querying audit logs with
 * various filters and criteria.</p>
 */
@Service
@Transactional(readOnly = true)
public class GetAuditLogService implements GetAuditLogUseCase {
    
    private final AuditLogRepositoryPort auditLogRepository;
    
    public GetAuditLogService(AuditLogRepositoryPort auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    @Override
    public AuditLog getById(AuditLogId id) {
        Objects.requireNonNull(id, "Audit log ID cannot be null");
        
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Audit log not found with ID: " + id.value()));
    }
    
    @Override
    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }
    
    @Override
    public List<AuditLog> getByUsername(String username) {
        validateUsername(username);
        return auditLogRepository.findByUsername(username);
    }
    
    @Override
    public List<AuditLog> getByEntity(EntityType entityType, String entityId) {
        Objects.requireNonNull(entityType, "Entity type cannot be null");
        validateEntityId(entityId);
        return auditLogRepository.findByEntity(entityType, entityId);
    }
    
    @Override
    public List<AuditLog> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        return auditLogRepository.findByTimestampBetween(startTime, endTime);
    }
    
    @Override
    public List<AuditLog> getByUsernameAndTimeRange(
            String username, 
            LocalDateTime startTime, 
            LocalDateTime endTime) {
        validateUsername(username);
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        return auditLogRepository.findByUsernameAndTimestampBetween(
                username, startTime, endTime);
    }
    
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
    }
    
    private void validateEntityId(String entityId) {
        if (entityId == null || entityId.isBlank()) {
            throw new IllegalArgumentException("Entity ID cannot be null or blank");
        }
    }
}
