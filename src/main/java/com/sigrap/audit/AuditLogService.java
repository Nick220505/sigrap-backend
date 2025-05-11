package com.sigrap.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service for audit logging.
 * Provides methods for recording user actions and retrieving audit logs.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final AuditLogMapper auditLogMapper;
  private final ObjectMapper objectMapper;

  /**
   * Logs a user action in the audit log.
   *
   * @param userId The ID of the user performing the action
   * @param username The username of the user performing the action
   * @param action The action being performed
   * @param entityName The name of the entity being affected
   * @param entityId The ID of the entity being affected
   * @param oldValue The previous state of the entity (can be null)
   * @param newValue The new state of the entity (can be null)
   * @return AuditLogInfo containing the created audit log entry
   */
  @Transactional
  public AuditLogInfo log(
    Long userId,
    String username,
    String action,
    String entityName,
    String entityId,
    Object oldValue,
    Object newValue
  ) {
    String ipAddress = getClientIpAddress();

    AuditLog auditLog = AuditLog.builder()
      .userId(userId)
      .username(username)
      .action(action)
      .entityName(entityName)
      .entityId(entityId)
      .oldValue(convertToJsonString(oldValue))
      .newValue(convertToJsonString(newValue))
      .timestamp(LocalDateTime.now())
      .ipAddress(ipAddress)
      .build();

    AuditLog savedAuditLog = auditLogRepository.save(auditLog);
    return auditLogMapper.toInfo(savedAuditLog);
  }

  /**
   * Retrieves all audit logs with pagination.
   *
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findAll(Pageable pageable) {
    return auditLogRepository.findAll(pageable).map(auditLogMapper::toInfo);
  }

  /**
   * Retrieves audit logs for a specific user with pagination.
   *
   * @param userId The ID of the user to find logs for
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByUserId(Long userId, Pageable pageable) {
    return auditLogRepository
      .findByUserIdOrderByTimestampDesc(userId, pageable)
      .map(auditLogMapper::toInfo);
  }

  /**
   * Retrieves audit logs for a specific entity with pagination.
   *
   * @param entityName The name of the entity to find logs for
   * @param entityId The ID of the entity to find logs for
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByEntityNameAndEntityId(
    String entityName,
    String entityId,
    Pageable pageable
  ) {
    return auditLogRepository
      .findByEntityNameAndEntityIdOrderByTimestampDesc(
        entityName,
        entityId,
        pageable
      )
      .map(auditLogMapper::toInfo);
  }

  /**
   * Retrieves audit logs for a specific action with pagination.
   *
   * @param action The action to find logs for
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByAction(String action, Pageable pageable) {
    return auditLogRepository
      .findByActionOrderByTimestampDesc(action, pageable)
      .map(auditLogMapper::toInfo);
  }

  /**
   * Retrieves audit logs within a date range with pagination.
   *
   * @param startTime The start of the date range
   * @param endTime The end of the date range
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> findByDateRange(
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  ) {
    return auditLogRepository
      .findByTimestampBetweenOrderByTimestampDesc(startTime, endTime, pageable)
      .map(auditLogMapper::toInfo);
  }

  /**
   * Searches audit logs based on multiple criteria with pagination.
   *
   * @param userId The ID of the user (or null for any user)
   * @param entityName The name of the entity (or null for any entity)
   * @param action The action (or null for any action)
   * @param startTime The start of the time range (or null for no start limit)
   * @param endTime The end of the time range (or null for no end limit)
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public Page<AuditLogInfo> search(
    Long userId,
    String entityName,
    String action,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Pageable pageable
  ) {
    return auditLogRepository
      .searchAuditLogs(userId, entityName, action, startTime, endTime, pageable)
      .map(auditLogMapper::toInfo);
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
   * Converts an object to its JSON string representation.
   *
   * @param value The object to convert
   * @return JSON string representation or null if the object is null
   */
  private String convertToJsonString(Object value) {
    if (value == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      return "Error serializing object: " + e.getMessage();
    }
  }

  /**
   * Gets the client IP address from the current request.
   *
   * @return The client IP address or "unknown" if not available
   */
  private String getClientIpAddress() {
    try {
      ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (
          ipAddress == null ||
          ipAddress.isEmpty() ||
          "unknown".equalsIgnoreCase(ipAddress)
        ) {
          ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (
          ipAddress == null ||
          ipAddress.isEmpty() ||
          "unknown".equalsIgnoreCase(ipAddress)
        ) {
          ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (
          ipAddress == null ||
          ipAddress.isEmpty() ||
          "unknown".equalsIgnoreCase(ipAddress)
        ) {
          ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
          ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
      }
    } catch (Exception e) {}

    return "unknown";
  }
}
