package com.sigrap.audit.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an audit log entry.
 * This is a pure POJO with no framework dependencies.
 * 
 * <p>An audit log records important actions performed in the system,
 * including who performed the action, what was done, when it occurred,
 * and additional context like IP address and user agent.</p>
 */
public class AuditLog {
    
    private final AuditLogId id;
    private final String username;
    private final AuditAction action;
    private final EntityType entityType;
    private final String entityId;
    private final LocalDateTime timestamp;
    private final String sourceIp;
    private final String userAgent;
    private final String details;
    private final AuditStatus status;
    private final Long durationMs;
    
    /**
     * Constructor for creating a new audit log (without ID).
     * Used when creating a new audit log before persistence.
     */
    public AuditLog(
            String username,
            AuditAction action,
            EntityType entityType,
            String entityId,
            LocalDateTime timestamp,
            String sourceIp,
            String userAgent,
            String details,
            AuditStatus status,
            Long durationMs) {
        this(null, username, action, entityType, entityId, timestamp, 
             sourceIp, userAgent, details, status, durationMs);
    }
    
    /**
     * Full constructor for reconstituting an audit log from persistence.
     * 
     * @param id The audit log ID (may be null for new logs)
     * @param username The username who performed the action
     * @param action The action that was performed
     * @param entityType The type of entity affected
     * @param entityId The ID of the entity affected (may be null)
     * @param timestamp When the action occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param details Additional details about the action (may be null)
     * @param status The status of the action
     * @param durationMs The duration of the operation in milliseconds (may be null)
     */
    public AuditLog(
            AuditLogId id,
            String username,
            AuditAction action,
            EntityType entityType,
            String entityId,
            LocalDateTime timestamp,
            String sourceIp,
            String userAgent,
            String details,
            AuditStatus status,
            Long durationMs) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.action = Objects.requireNonNull(action, "Action cannot be null");
        this.entityType = Objects.requireNonNull(entityType, "Entity type cannot be null");
        this.entityId = entityId;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
        this.details = details;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.durationMs = durationMs;
    }
    
    /**
     * Checks if this is a new audit log (not yet persisted).
     * 
     * @return true if the audit log has no ID
     */
    public boolean isNew() {
        return id == null;
    }
    
    /**
     * Checks if the action was successful.
     * 
     * @return true if the status is SUCCESS
     */
    public boolean isSuccessful() {
        return status == AuditStatus.SUCCESS;
    }
    
    /**
     * Checks if the action failed.
     * 
     * @return true if the status is ERROR
     */
    public boolean isFailed() {
        return status == AuditStatus.ERROR;
    }
    
    // Getters
    
    public AuditLogId getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public AuditAction getAction() {
        return action;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public String getEntityId() {
        return entityId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getSourceIp() {
        return sourceIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public String getDetails() {
        return details;
    }
    
    public AuditStatus getStatus() {
        return status;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", entityType=" + entityType +
                ", entityId='" + entityId + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}
