package com.sigrap.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   * Retrieves all audit logs with pagination.
   *
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all audit logs",
    description = "Retrieves a paginated list of all audit logs in the system"
  )
  public Page<AuditLogInfo> getAllAuditLogs(Pageable pageable) {
    return auditLogService.findAll(pageable);
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
  public AuditLogInfo getAuditLogById(@PathVariable Long id) {
    return auditLogService.findById(id);
  }

  /**
   * Searches audit logs based on various filters with pagination.
   *
   * @param userId The ID of the user (optional)
   * @param entityName The name of the entity (optional)
   * @param entityId The ID of the entity (optional)
   * @param action The action performed (optional)
   * @param startTime The start of the time range (optional)
   * @param endTime The end of the time range (optional)
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping("/search")
  @Operation(
    summary = "Search audit logs",
    description = "Searches for audit logs matching the specified criteria"
  )
  public Page<AuditLogInfo> searchAuditLogs(
    @RequestParam(required = false) Long userId,
    @RequestParam(required = false) String entityName,
    @RequestParam(required = false) String entityId,
    @RequestParam(required = false) String action,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startTime,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endTime,
    Pageable pageable
  ) {
    if (entityId != null && entityName == null) {
      throw new IllegalArgumentException(
        "Entity name must be provided when searching by entity ID"
      );
    }

    return auditLogService.search(
      userId,
      entityName,
      action,
      startTime,
      endTime,
      pageable
    );
  }

  /**
   * Retrieves audit logs for a specific user with pagination.
   *
   * @param userId The ID of the user
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping("/users/{userId}")
  @Operation(
    summary = "Get audit logs by user",
    description = "Retrieves a paginated list of audit logs for a specific user"
  )
  public Page<AuditLogInfo> getAuditLogsByUser(
    @PathVariable Long userId,
    Pageable pageable
  ) {
    return auditLogService.findByUserId(userId, pageable);
  }

  /**
   * Retrieves audit logs for a specific entity with pagination.
   *
   * @param entityName The name of the entity
   * @param entityId The ID of the entity
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping("/entities/{entityName}/{entityId}")
  @Operation(
    summary = "Get audit logs by entity",
    description = "Retrieves a paginated list of audit logs for a specific entity"
  )
  public Page<AuditLogInfo> getAuditLogsByEntity(
    @PathVariable String entityName,
    @PathVariable String entityId,
    Pageable pageable
  ) {
    return auditLogService.findByEntityNameAndEntityId(
      entityName,
      entityId,
      pageable
    );
  }

  /**
   * Retrieves audit logs for a specific action with pagination.
   *
   * @param action The action
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping("/actions/{action}")
  @Operation(
    summary = "Get audit logs by action",
    description = "Retrieves a paginated list of audit logs for a specific action"
  )
  public Page<AuditLogInfo> getAuditLogsByAction(
    @PathVariable String action,
    Pageable pageable
  ) {
    return auditLogService.findByAction(action, pageable);
  }

  /**
   * Retrieves audit logs within a date range with pagination.
   *
   * @param startTime The start of the date range
   * @param endTime The end of the date range
   * @param pageable Pagination parameters
   * @return Page of AuditLogInfo DTOs
   */
  @GetMapping("/timerange")
  @Operation(
    summary = "Get audit logs by time range",
    description = "Retrieves a paginated list of audit logs within a specified time range"
  )
  public Page<AuditLogInfo> getAuditLogsByTimeRange(
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startTime,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endTime,
    Pageable pageable
  ) {
    return auditLogService.findByDateRange(startTime, endTime, pageable);
  }
}
