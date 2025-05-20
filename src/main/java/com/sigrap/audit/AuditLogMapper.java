package com.sigrap.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between AuditLog entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
@RequiredArgsConstructor
public class AuditLogMapper {

  private final ObjectMapper objectMapper;

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

    AuditLogInfo info = AuditLogInfo.builder()
      .id(auditLog.getId())
      .username(auditLog.getUsername())
      .action(auditLog.getAction())
      .entityName(auditLog.getEntityName())
      .entityId(auditLog.getEntityId())
      .timestamp(auditLog.getTimestamp())
      .sourceIp(auditLog.getSourceIp())
      .userAgent(auditLog.getUserAgent())
      .details(auditLog.getDetails())
      .status(auditLog.getStatus())
      .durationMs(auditLog.getDurationMs())
      .build();

    parseDetailsForValueChanges(info);

    return info;
  }

  /**
   * Parses the details JSON string to extract old and new values.
   *
   * @param info The AuditLogInfo to populate
   */
  private void parseDetailsForValueChanges(AuditLogInfo info) {
    if (info.getDetails() == null || info.getDetails().isEmpty()) {
      return;
    }

    try {
      JsonNode detailsNode = objectMapper.readTree(info.getDetails());

      if (detailsNode.has("before") && detailsNode.has("after")) {
        info.setOldValue(detailsNode.get("before"));
        info.setNewValue(detailsNode.get("after"));
      } else if (detailsNode.has("args") && detailsNode.has("result")) {
        info.setOldValue(detailsNode.get("args"));
        info.setNewValue(detailsNode.get("result"));
      }
    } catch (JsonProcessingException e) {}
  }

  /**
   * Converts a list of AuditLog entities to a list of AuditLogInfo DTOs.
   *
   * @param auditLogs List of AuditLog entities to convert
   * @return List of AuditLogInfo DTOs
   */
  public List<AuditLogInfo> toInfoList(List<AuditLog> auditLogs) {
    return auditLogs.stream().map(this::toInfo).collect(Collectors.toList());
  }

  /**
   * Converts a Page of AuditLog entities to a Page of AuditLogInfo DTOs.
   *
   * @param auditLogPage Page of AuditLog entities to convert
   * @return Page of AuditLogInfo DTOs
   */
  public Page<AuditLogInfo> toInfoPage(Page<AuditLog> auditLogPage) {
    return auditLogPage.map(this::toInfo);
  }

  /**
   * Converts an AuditEvent to an AuditLog entity.
   *
   * @param auditEvent The AuditEvent to convert
   * @return AuditLog entity
   */
  public AuditLog fromEvent(AuditEvent auditEvent) {
    if (auditEvent == null) {
      return null;
    }

    return AuditLog.builder()
      .username(auditEvent.getUsername())
      .action(auditEvent.getAction())
      .entityName(auditEvent.getEntityName())
      .entityId(auditEvent.getEntityId())
      .timestamp(auditEvent.getTimestamp())
      .sourceIp(auditEvent.getSourceIp())
      .userAgent(auditEvent.getUserAgent())
      .details(auditEvent.getDetails())
      .status(auditEvent.getStatus())
      .durationMs(auditEvent.getDurationMs())
      .build();
  }
}
