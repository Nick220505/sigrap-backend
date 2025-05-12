package com.sigrap.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
   * Retrieves audit logs for a specific user.
   *
   * @param userId The ID of the user to find logs for
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findByUserId(Long userId) {
    return auditLogRepository
      .findByUserIdOrderByTimestampDesc(userId)
      .stream()
      .map(auditLogMapper::toInfo)
      .toList();
  }

  /**
   * Retrieves audit logs for a specific entity.
   *
   * @param entityName The name of the entity to find logs for
   * @param entityId The ID of the entity to find logs for
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findByEntityNameAndEntityId(
    String entityName,
    String entityId
  ) {
    return auditLogRepository
      .findByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId)
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
   * @param endTime The end of the date range
   * @return List of AuditLogInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<AuditLogInfo> findByDateRange(
    LocalDateTime startTime,
    LocalDateTime endTime
  ) {
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
