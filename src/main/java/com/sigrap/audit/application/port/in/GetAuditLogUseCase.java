package com.sigrap.audit.application.port.in;

import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for retrieving audit log entries.
 * This interface defines the contract for querying audit logs.
 */
public interface GetAuditLogUseCase {
    
    /**
     * Gets an audit log by its ID.
     * 
     * @param id The audit log ID
     * @return The audit log
     * @throws IllegalArgumentException if id is null
     * @throws RuntimeException if audit log not found
     */
    AuditLog getById(AuditLogId id);
    
    /**
     * Gets all audit logs.
     * 
     * @return List of all audit logs
     */
    List<AuditLog> getAll();
    
    /**
     * Gets audit logs by username.
     * 
     * @param username The username to search for
     * @return List of audit logs for the user
     * @throws IllegalArgumentException if username is null or blank
     */
    List<AuditLog> getByUsername(String username);
    
    /**
     * Gets audit logs by entity.
     * 
     * @param entityType The entity type
     * @param entityId The entity ID
     * @return List of audit logs for the entity
     * @throws IllegalArgumentException if entityType is null or entityId is blank
     */
    List<AuditLog> getByEntity(EntityType entityType, String entityId);
    
    /**
     * Gets audit logs within a time range.
     * 
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit logs within the time range
     * @throws IllegalArgumentException if startTime or endTime is null
     */
    List<AuditLog> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Gets audit logs by username within a time range.
     * 
     * @param username The username to search for
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit logs for the user within the time range
     * @throws IllegalArgumentException if any parameter is null or username is blank
     */
    List<AuditLog> getByUsernameAndTimeRange(
            String username, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
}
