package com.sigrap.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for audit log management.
 * Provides endpoints for retrieving and searching audit logs.
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log management API")
public class AuditLogController {

  private final AuditLogService auditLogService;

  /**
   * Retrieves all audit logs.
   *
   * @return List of AuditLogInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all audit logs",
    description = "Retrieves a list of all audit logs in the system"
  )
  public List<AuditLogInfo> findAll() {
    return auditLogService.findAll();
  }

  /**
   * Retrieves an audit log by ID.
   *
   * @param id The ID of the audit log to retrieve
   * @return AuditLogInfo DTO
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get audit log by ID",
    description = "Retrieves a specific audit log by its unique identifier"
  )
  public AuditLogInfo findById(@PathVariable Long id) {
    return auditLogService.findById(id);
  }

  /**
   * Retrieves audit logs for a specific user.
   *
   * @param userId The ID of the user
   * @return List of AuditLogInfo DTOs
   */
  @GetMapping("/users/{userId}")
  @Operation(
    summary = "Get audit logs by user",
    description = "Retrieves a list of audit logs for a specific user"
  )
  public List<AuditLogInfo> findByUserId(@PathVariable Long userId) {
    return auditLogService.findByUserId(userId);
  }

  /**
   * Retrieves audit logs for a specific entity.
   *
   * @param entityName The name of the entity
   * @param entityId The ID of the entity
   * @return List of AuditLogInfo DTOs
   */
  @GetMapping("/entities/{entityName}/{entityId}")
  @Operation(
    summary = "Get audit logs by entity",
    description = "Retrieves a list of audit logs for a specific entity"
  )
  public List<AuditLogInfo> findByEntity(
    @PathVariable String entityName,
    @PathVariable String entityId
  ) {
    return auditLogService.findByEntityNameAndEntityId(entityName, entityId);
  }

  /**
   * Retrieves audit logs for a specific action.
   *
   * @param action The action
   * @return List of AuditLogInfo DTOs
   */
  @GetMapping("/actions/{action}")
  @Operation(
    summary = "Get audit logs by action",
    description = "Retrieves a list of audit logs for a specific action"
  )
  public List<AuditLogInfo> findByAction(@PathVariable String action) {
    return auditLogService.findByAction(action);
  }

  /**
   * Retrieves audit logs within a date range.
   *
   * @param startTime The start of the date range
   * @param endTime The end of the date range
   * @return List of AuditLogInfo DTOs
   */
  @GetMapping("/timerange")
  @Operation(
    summary = "Get audit logs by time range",
    description = "Retrieves a list of audit logs within a specified time range"
  )
  public List<AuditLogInfo> findByTimeRange(
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startTime,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endTime
  ) {
    return auditLogService.findByDateRange(startTime, endTime);
  }
}
