package com.sigrap.audit;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AuditLog entity operations.
 * Provides methods for audit log-specific database operations beyond basic CRUD.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
  /**
   * Finds audit logs by user ID.
   *
   * @param userId The ID of the user to search logs for
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByUserIdOrderByTimestampDesc(
    Long userId,
    Pageable pageable
  );

  /**
   * Finds audit logs by user ID and time range.
   *
   * @param userId The ID of the user to search logs for
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
    Long userId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  );

  /**
   * Finds audit logs by entity name and entity ID.
   *
   * @param entityName The name of the entity to search logs for
   * @param entityId The ID of the entity to search logs for
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(
    String entityName,
    String entityId,
    Pageable pageable
  );

  /**
   * Finds audit logs by action type.
   *
   * @param action The action to search logs for
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByActionOrderByTimestampDesc(
    String action,
    Pageable pageable
  );

  /**
   * Finds audit logs by time range.
   *
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  );

  /**
   * Searches audit logs based on multiple criteria.
   *
   * @param userId The ID of the user (or null for any user)
   * @param entityName The name of the entity (or null for any entity)
   * @param action The action (or null for any action)
   * @param startTime The start of the time range (or null for no start limit)
   * @param endTime The end of the time range (or null for no end limit)
   * @param pageable Pagination parameters
   * @return Page of matching audit logs
   */
  @Query(
    "SELECT a FROM AuditLog a WHERE " +
    "(:userId IS NULL OR a.userId = :userId) AND " +
    "(:entityName IS NULL OR a.entityName = :entityName) AND " +
    "(:action IS NULL OR a.action = :action) AND " +
    "(:startTime IS NULL OR a.timestamp >= :startTime) AND " +
    "(:endTime IS NULL OR a.timestamp <= :endTime) " +
    "ORDER BY a.timestamp DESC"
  )
  Page<AuditLog> searchAuditLogs(
    Long userId,
    String entityName,
    String action,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  );
}
