package com.sigrap.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for comprehensive audit logging.
 * Provides methods for recording user actions and retrieving audit logs with rich filtering capabilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final AuditLogMapper auditLogMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final ObjectMapper objectMapper;

  /**
   * Logs a user action in the audit log.
   *
   * @param username   The username of the user performing the action
   * @param action     The action being performed
   * @param entityName The name of the entity being affected
   * @return AuditLogInfo containing the created audit log entry
   */
  @Transactional
  public AuditLogInfo log(String username, String action, String entityName) {
    return log(username, action, entityName, null, null, null, null);
  }

  /**
   * Logs a user action in the audit log with entity ID.
   *
   * @param username   The username of the user performing the action
   * @param action     The action being performed
   * @param entityName The name of the entity being affected
   * @param entityId   The ID of the entity being affected
   * @return AuditLogInfo containing the created audit log entry
   */
  @Transactional
  public AuditLogInfo log(
    String username,
    String action,
    String entityName,
    String entityId
  ) {
    return log(username, action, entityName, entityId, null, null, null);
  }

  /**
   * Logs a user action in the audit log with comprehensive details.
   *
   * @param username   The username of the user performing the action
   * @param action     The action being performed
   * @param entityName The name of the entity being affected
   * @param entityId   The ID of the entity being affected
   * @param sourceIp   The IP address of the client
   * @param userAgent  The user agent string of the client
   * @param details    Additional details about the action
   * @return AuditLogInfo containing the created audit log entry
   */
  @Transactional
  public AuditLogInfo log(
    String username,
    String action,
    String entityName,
    String entityId,
    String sourceIp,
    String userAgent,
    String details
  ) {
    AuditLog auditLog = AuditLog.builder()
      .username(username)
      .action(action)
      .entityName(entityName)
      .entityId(entityId)
      .timestamp(LocalDateTime.now(ZoneId.of("America/Bogota")))
      .sourceIp(sourceIp)
      .userAgent(userAgent)
      .details(details)
      .status("SUCCESS")
      .build();

    AuditLog savedAuditLog = auditLogRepository.save(auditLog);
    return auditLogMapper.toInfo(savedAuditLog);
  }

  /**
   * Publishes an audit event to be logged asynchronously.
   *
   * @param auditEvent The audit event to publish
   */
  public void publishAuditEvent(AuditEvent auditEvent) {
    eventPublisher.publishEvent(auditEvent);
  }

  /**
   * Handles audit events asynchronously.
   *
   * @param auditEvent The audit event to handle
   */
  @Transactional
  public void handleAuditEvent(AuditEvent auditEvent) {
    try {
      AuditLog auditLog = auditLogMapper.fromEvent(auditEvent);
      auditLogRepository.save(auditLog);
      log.debug(
        "Audit logged: {} - {} - {}",
        auditEvent.getUsername(),
        auditEvent.getAction(),
        auditEvent.getEntityName()
      );
    } catch (Exception e) {
      log.error("Failed to log audit event", e);
    }
  }

  /**
   * Retrieves audit logs with pagination.
   *
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findAll(Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findAllByOrderByTimestampDesc(pageable)
    );
  }

  /**
   * Retrieves audit logs for a specific username with pagination.
   *
   * @param username The username to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByUsername(String username, Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByUsernameOrderByTimestampDesc(username, pageable)
    );
  }

  /**
   * Retrieves audit logs for a specific action with pagination.
   *
   * @param action The action to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByAction(String action, Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByActionOrderByTimestampDesc(action, pageable)
    );
  }

  /**
   * Retrieves audit logs for a specific entity name with pagination.
   *
   * @param entityName The entity name to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByEntityName(
    String entityName,
    Pageable pageable
  ) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByEntityNameOrderByTimestampDesc(
        entityName,
        pageable
      )
    );
  }

  /**
   * Retrieves audit logs for a specific entity ID with pagination.
   *
   * @param entityId The entity ID to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByEntityId(String entityId, Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByEntityIdOrderByTimestampDesc(entityId, pageable)
    );
  }

  /**
   * Retrieves audit logs for a specific entity name and ID with pagination.
   *
   * @param entityName The entity name to find logs for
   * @param entityId The entity ID to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByEntityNameAndEntityId(
    String entityName,
    String entityId,
    Pageable pageable
  ) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(
        entityName,
        entityId,
        pageable
      )
    );
  }

  /**
   * Retrieves audit logs within a date range with pagination.
   *
   * @param startTime The start of the date range
   * @param endTime   The end of the date range
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByDateRange(
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  ) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(
        startTime,
        endTime,
        pageable
      )
    );
  }

  /**
   * Retrieves audit logs by source IP with pagination.
   *
   * @param sourceIp The source IP to find logs for
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findBySourceIp(String sourceIp, Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findBySourceIpOrderByTimestampDesc(sourceIp, pageable)
    );
  }

  /**
   * Retrieves audit logs with error status with pagination.
   *
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findErrors(Pageable pageable) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByStatusNotOrderByTimestampDesc(
        "SUCCESS",
        pageable
      )
    );
  }

  /**
   * Retrieves audit logs matching the specified criteria using Spring Data JPA Specifications.
   *
   * @param spec The specification to apply
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findBySpecification(
    Specification<AuditLog> spec,
    Pageable pageable
  ) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findAll(spec, pageable)
    );
  }

  /**
   * Retrieves audit logs for a specific entity name within a date range with pagination.
   *
   * @param entityName The entity name to find logs for
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @param pageable Pagination information
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByEntityNameAndTimestampBetween(
    String entityName,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  ) {
    return auditLogMapper.toInfoPage(
      auditLogRepository.findByEntityNameAndTimestampBetweenOrderByTimestampDesc(
        entityName,
        startTime,
        endTime,
        pageable
      )
    );
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
      .orElseThrow(() ->
        new EntityNotFoundException("Audit log not found: " + id)
      );

    return auditLogMapper.toInfo(auditLog);
  }

  /**
   * Retrieves recent actions by a specific user.
   *
   * @param username The username to search for
   * @param pageable Pagination information
   * @return List of audit logs for the user
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findRecentActionsByUser(
    String username,
    Pageable pageable
  ) {
    List<AuditLog> auditLogs = auditLogRepository.findRecentActionsByUser(
      username,
      pageable
    );
    return auditLogMapper.toInfoList(auditLogs);
  }

  /**
   * Counts actions by type within a date range.
   *
   * @param startTime The start of the time range
   * @param endTime The end of the time range
   * @return List of action counts by action type
   */
  @Transactional(readOnly = true)
  public List<Object[]> countActionsByType(
    LocalDateTime startTime,
    LocalDateTime endTime
  ) {
    return auditLogRepository.countActionsByType(startTime, endTime);
  }
}
