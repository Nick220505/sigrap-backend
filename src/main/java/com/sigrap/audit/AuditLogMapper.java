package com.sigrap.audit;

import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between AuditLog entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
public class AuditLogMapper {

  /**
   * Converts an AuditLog entity to an AuditLogInfo DTO.
   *
   * @param auditLog The AuditLog entity to convert
   * @return AuditLogInfo containing the audit log data
   */
  public AuditLogInfo toInfo(AuditLog auditLog) {
    if (auditLog == null) {
      return null;
    }

    return AuditLogInfo.builder()
      .id(auditLog.getId())
      .userId(auditLog.getUserId())
      .username(auditLog.getUsername())
      .action(auditLog.getAction())
      .entityName(auditLog.getEntityName())
      .entityId(auditLog.getEntityId())
      .oldValue(auditLog.getOldValue())
      .newValue(auditLog.getNewValue())
      .timestamp(auditLog.getTimestamp())
      .ipAddress(auditLog.getIpAddress())
      .build();
  }
}
