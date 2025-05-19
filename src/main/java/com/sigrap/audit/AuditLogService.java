package com.sigrap.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service for audit logging.
 * Provides methods for recording user actions and retrieving audit logs.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final AuditLogMapper auditLogMapper;

  /**
   * Logs a user action in the audit log.
   *
   * @param username   The username of the user performing the action
   * @param action     The action being performed
   * @param entityName The name of the entity being affected
   * @return AuditLogInfo containing the created audit log entry
   */
  @Transactional
  public AuditLogInfo log(
      String username,
      String action,
      String entityName) {
    AuditLog auditLog = AuditLog.builder()
        .username(username)
        .action(action)
        .entityName(entityName)
        .timestamp(LocalDateTime.now())
        .build();

    AuditLog savedAuditLog = auditLogRepository.save(auditLog);
    return auditLogMapper.toInfo(savedAuditLog);
  }

  /**
   * Retrieves all audit logs.
   *
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findAll() {
    return auditLogRepository
        .findAllByOrderByTimestampDesc()
        .stream()
        .map(auditLogMapper::toInfo)
        .toList();
  }

  /**
   * Retrieves audit logs for a specific action.
   *
   * @param action The action to find logs for
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findByAction(String action) {
    return auditLogRepository
        .findByActionOrderByTimestampDesc(action)
        .stream()
        .map(auditLogMapper::toInfo)
        .toList();
  }

  /**
   * Retrieves audit logs within a date range.
   *
   * @param startTime The start of the date range
   * @param endTime   The end of the date range
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findByDateRange(
      LocalDateTime startTime,
      LocalDateTime endTime) {
    return auditLogRepository
        .findByTimestampBetweenOrderByTimestampDesc(startTime, endTime)
        .stream()
        .map(auditLogMapper::toInfo)
        .toList();
  }

  /**
   * Retrieves an audit log by its ID.
   *
   * @param id The ID of the audit log to retrieve
   * @return AuditLogInfo containing the audit log data
   * @throws EntityNotFoundException if the audit log is not found
   */
  @Transactional(readOnly = true)
  public AuditLogInfo findById(Long id) {
    AuditLog auditLog = auditLogRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Audit log not found: " + id));

    return auditLogMapper.toInfo(auditLog);
  }
}
