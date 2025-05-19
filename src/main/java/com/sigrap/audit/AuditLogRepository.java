package com.sigrap.audit;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for AuditLog entity operations.
 * Provides methods for audit log-specific database operations beyond basic
 * CRUD.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
  /**
   * Finds all audit logs ordered by timestamp descending.
   *
   * @return List of audit logs
   */
  List<AuditLog> findAllByOrderByTimestampDesc();

  /**
   * Finds audit logs by action type.
   *
   * @param action The action to search logs for
   * @return List of matching audit logs
   */
  List<AuditLog> findByActionOrderByTimestampDesc(String action);

  /**
   * Finds audit logs by time range.
   *
   * @param startTime The start of the time range
   * @param endTime   The end of the time range
   * @return List of matching audit logs
   */
  List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
      LocalDateTime startTime,
      LocalDateTime endTime);
}
