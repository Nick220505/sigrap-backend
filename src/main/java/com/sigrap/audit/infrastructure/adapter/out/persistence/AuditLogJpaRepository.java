package com.sigrap.audit.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for audit log persistence.
 * 
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations
 * and custom query methods for audit logs.</p>
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {
    
    /**
     * Finds audit logs by username.
     * 
     * @param username The username to search for
     * @return List of audit log entities
     */
    List<AuditLogJpaEntity> findByUsername(String username);
    
    /**
     * Finds audit logs by entity type and ID.
     * 
     * @param entityType The entity type as a string
     * @param entityId The entity ID
     * @return List of audit log entities
     */
    List<AuditLogJpaEntity> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * Finds audit logs within a time range.
     * 
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit log entities
     */
    List<AuditLogJpaEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Finds audit logs by username within a time range.
     * 
     * @param username The username to search for
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of audit log entities
     */
    List<AuditLogJpaEntity> findByUsernameAndTimestampBetween(
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
     * 
     * @param cutoffDate The date before which logs should be deleted
     * @return The number of logs deleted
     */
    @Modifying
    @Query("DELETE FROM AuditLogJpaEntity a WHERE a.timestamp < :cutoffDate")
    long deleteByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
