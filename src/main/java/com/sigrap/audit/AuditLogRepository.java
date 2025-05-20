package com.sigrap.audit;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AuditLog entity operations.
 * Provides comprehensive methods for audit log retrieval and analysis.
 */
@Repository
public interface AuditLogRepository
  extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
  /**
   * Finds all audit logs ordered by timestamp descending.
   *
   * @param pageable Pagination information
   * @return Page of audit logs
   */
  Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

  /**
   * Finds audit logs by username.
   *
   * @param username The username to search for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByUsernameOrderByTimestampDesc(
    String username,
    Pageable pageable
  );

  /**
   * Finds audit logs by action type.
   *
   * @param action The action to search logs for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByActionOrderByTimestampDesc(
    String action,
    Pageable pageable
  );

  /**
   * Finds audit logs by entity name.
   *
   * @param entityName The entity name to search for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByEntityNameOrderByTimestampDesc(
    String entityName,
    Pageable pageable
  );

  /**
   * Finds audit logs by entity ID.
   *
   * @param entityId The entity ID to search for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByEntityIdOrderByTimestampDesc(
    String entityId,
    Pageable pageable
  );

  /**
   * Finds audit logs by entity name and entity ID.
   *
   * @param entityName The entity name to search for
   * @param entityId The entity ID to search for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(
    String entityName,
    String entityId,
    Pageable pageable
  );

  /**
   * Finds audit logs by time range.
   *
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  );

  /**
   * Finds audit logs that match the given source IP.
   *
   * @param sourceIp The source IP to search for
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findBySourceIpOrderByTimestampDesc(
    String sourceIp,
    Pageable pageable
  );

  /**
   * Finds audit logs with status indicating an error or failure.
   *
   * @param pageable Pagination information
   * @return Page of error audit logs
   */
  Page<AuditLog> findByStatusNotOrderByTimestampDesc(
    String status,
    Pageable pageable
  );

  /**
   * Finds audit logs for a specific entity across a time range.
   *
   * @param entityName The entity name to filter by
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  Page<AuditLog> findByEntityNameAndTimestampBetweenOrderByTimestampDesc(
    String entityName,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  );

  /**
   * Finds recent actions by a specific user.
   *
   * @param username The username to search for
   * @param limit Maximum number of records to return
   * @return List of most recent audit logs for the user
   */
  @Query(
    "SELECT a FROM AuditLog a WHERE a.username = :username ORDER BY a.timestamp DESC"
  )
  List<AuditLog> findRecentActionsByUser(
    @Param("username") String username,
    Pageable pageable
  );

  /**
   * Counts actions by type within a date range.
   *
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @return List of action counts by action type
   */
  @Query(
    "SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime GROUP BY a.action"
  )
  List<Object[]> countActionsByType(
    @Param("startTime") LocalDateTime startTime,
    @Param("endTime") LocalDateTime endTime
  );
}
