package com.sigrap.audit.infrastructure.adapter.out.persistence;

import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of the AuditLogRepositoryPort.
 * 
 * <p>This adapter translates between the domain model and JPA entities,
 * delegating to Spring Data JPA for actual persistence operations.</p>
 * 
 * <p>This adapter isolates the domain layer from JPA and Spring Data,
 * allowing the core business logic to remain framework-agnostic.</p>
 */
@Component
@Slf4j
public class AuditLogPersistenceAdapter implements AuditLogRepositoryPort {
    
    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogPersistenceMapper mapper;
    
    public AuditLogPersistenceAdapter(
            AuditLogJpaRepository jpaRepository,
            AuditLogPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public AuditLog save(AuditLog auditLog) {
        log.debug("Saving audit log: {}", auditLog);
        AuditLogJpaEntity entity = mapper.toJpaEntity(auditLog);
        AuditLogJpaEntity saved = jpaRepository.save(entity);
        AuditLog result = mapper.toDomain(saved);
        log.debug("Audit log saved with ID: {}", result.getId());
        return result;
    }
    
    @Override
    public Optional<AuditLog> findById(AuditLogId id) {
        log.debug("Finding audit log by ID: {}", id);
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public List<AuditLog> findAll() {
        log.debug("Finding all audit logs");
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<AuditLog> findByUsername(String username) {
        log.debug("Finding audit logs by username: {}", username);
        return jpaRepository.findByUsername(username).stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<AuditLog> findByEntity(EntityType entityType, String entityId) {
        log.debug("Finding audit logs by entity: {} - {}", entityType, entityId);
        return jpaRepository.findByEntityTypeAndEntityId(entityType.value(), entityId).stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Finding audit logs between {} and {}", startTime, endTime);
        return jpaRepository.findByTimestampBetween(startTime, endTime).stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<AuditLog> findByUsernameAndTimestampBetween(
            String username, 
            LocalDateTime startTime, 
            LocalDateTime endTime) {
        log.debug("Finding audit logs by username {} between {} and {}", username, startTime, endTime);
        return jpaRepository.findByUsernameAndTimestampBetween(username, startTime, endTime).stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public long countByUsername(String username) {
        log.debug("Counting audit logs by username: {}", username);
        return jpaRepository.countByUsername(username);
    }
    
    @Override
    public long deleteByTimestampBefore(LocalDateTime cutoffDate) {
        log.info("Deleting audit logs before: {}", cutoffDate);
        long deleted = jpaRepository.deleteByTimestampBefore(cutoffDate);
        log.info("Deleted {} audit logs", deleted);
        return deleted;
    }
}
