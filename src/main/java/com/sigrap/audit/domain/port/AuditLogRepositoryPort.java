package com.sigrap.audit.domain.port;

import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for audit log persistence.
 * Defines the contract for storing and retrieving audit logs.
 * This interface is defined in the domain layer and implemented in the infrastructure layer.
 */
public interface AuditLogRepositoryPort {
    
    /**
     * Saves an audit log entry.
     * 
     * @param auditLog The audit log to save
     * @return The saved audit log with generated ID
     */
    AuditLog save(AuditLog auditLog);
    
    /**
     * Finds an audit log by its ID.
     * 
     * @param id The audit log ID
     * @return An optional containing the audit log if found
     */
    Optional<AuditLog> findById(AuditLogId id);
    
    /**
     * Finds all audit logs.
     * 
     * @return List of all audit logs
     */
    List<AuditLog> findAll();
    
    /**
     * Finds audit logs by username.
     * 
     * @param username The username to search for
     * @return List of audit logs for the user
     */
    List<AuditLog> findByUsername(String username);
    
    /**
     * Finds audit logs by entity type and ID.
     * 
     * @param entityType The entity type
     * @param entityId The entity ID
     * @return List of audit logs for the entity
     */
    List<AuditLog> findByEntity(EntityType entityType, String entityId);
    
    /**
     * Finds audit logs within a time range.
     * 
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit logs within the time range
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Finds audit logs by username within a time range.
     * 
     * @param username The username to search for
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit logs for the user within the time range
     */
    List<AuditLog> findByUsernameAndTimestampBetween(
            String username, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    /**
     * Counts audit logs by username.
     * 
     * @param username The username to count logs for
     * @return The count of audit logs
     */
    long countByUsername(String username);
    
    /**
     * Deletes audit logs older than the specified date.
     * Used for audit log retention policies.
     * 
     * @param cutoffDate The date before which logs should be deleted
     * @return The number of logs deleted
     */
    long deleteByTimestampBefore(LocalDateTime cutoffDate);
}
