package com.sigrap.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for audit log queries and operations.
 * Provides endpoints for retrieving and filtering audit logs.
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(
  name = "Audit Logs",
  description = "API endpoints for querying system audit logs"
)
@PreAuthorize("isAuthenticated()")
public class AuditLogController {

  private final AuditLogService auditLogService;

  /**
   * Retrieves all audit logs with pagination.
   *
   * @param pageable Pagination information
   * @return Page of audit logs
   */
  @GetMapping
  @Operation(
    summary = "Get all audit logs",
    description = "Retrieves all audit logs with pagination"
  )
  public Page<AuditLogInfo> getAll(Pageable pageable) {
    return auditLogService.findAll(pageable);
  }

  /**
   * Retrieves an audit log by its ID.
   *
   * @param id The ID of the audit log
   * @return The audit log information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get audit log by ID",
    description = "Retrieves a specific audit log by its ID"
  )
  public ResponseEntity<AuditLogInfo> getById(
    @Parameter(
      description = "Audit log ID",
      required = true
    ) @PathVariable Long id
  ) {
    AuditLogInfo auditLog = auditLogService.findById(id);
    return ResponseEntity.ok(auditLog);
  }

  /**
   * Retrieves audit logs filtered by username with pagination.
   *
   * @param username The username to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-user")
  @Operation(
    summary = "Get audit logs by username",
    description = "Retrieves audit logs filtered by username"
  )
  @PreAuthorize("hasRole('ADMINISTRATOR') or #username == authentication.name")
  public Page<AuditLogInfo> getByUsername(
    @Parameter(
      description = "Username",
      required = true
    ) @RequestParam String username,
    Pageable pageable
  ) {
    return auditLogService.findByUsername(username, pageable);
  }

  /**
   * Retrieves audit logs filtered by action with pagination.
   *
   * @param action The action to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-action")
  @Operation(
    summary = "Get audit logs by action",
    description = "Retrieves audit logs filtered by action type"
  )
  public Page<AuditLogInfo> getByAction(
    @Parameter(
      description = "Action type",
      required = true
    ) @RequestParam String action,
    Pageable pageable
  ) {
    return auditLogService.findByAction(action, pageable);
  }

  /**
   * Retrieves audit logs filtered by entity name with pagination.
   *
   * @param entityName The entity name to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-entity")
  @Operation(
    summary = "Get audit logs by entity name",
    description = "Retrieves audit logs filtered by entity name"
  )
  public Page<AuditLogInfo> getByEntityName(
    @Parameter(
      description = "Entity name",
      required = true
    ) @RequestParam String entityName,
    Pageable pageable
  ) {
    return auditLogService.findByEntityName(entityName, pageable);
  }

  /**
   * Retrieves audit logs filtered by entity ID with pagination.
   *
   * @param entityId The entity ID to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-entity-id")
  @Operation(
    summary = "Get audit logs by entity ID",
    description = "Retrieves audit logs filtered by entity ID"
  )
  public Page<AuditLogInfo> getByEntityId(
    @Parameter(
      description = "Entity ID",
      required = true
    ) @RequestParam String entityId,
    Pageable pageable
  ) {
    return auditLogService.findByEntityId(entityId, pageable);
  }

  /**
   * Retrieves audit logs filtered by entity name and ID with pagination.
   *
   * @param entityName The entity name to filter by
   * @param entityId The entity ID to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-entity-and-id")
  @Operation(
    summary = "Get audit logs by entity name and ID",
    description = "Retrieves audit logs filtered by entity name and ID"
  )
  public Page<AuditLogInfo> getByEntityNameAndId(
    @Parameter(
      description = "Entity name",
      required = true
    ) @RequestParam String entityName,
    @Parameter(
      description = "Entity ID",
      required = true
    ) @RequestParam String entityId,
    Pageable pageable
  ) {
    return auditLogService.findByEntityNameAndEntityId(
      entityName,
      entityId,
      pageable
    );
  }

  /**
   * Retrieves audit logs filtered by date range with pagination.
   *
   * @param startDate The start date of the range
   * @param endDate The end date of the range
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-date-range")
  @Operation(
    summary = "Get audit logs by date range",
    description = "Retrieves audit logs filtered by date range"
  )
  public Page<AuditLogInfo> getByDateRange(
    @Parameter(
      description = "Start date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    Pageable pageable
  ) {
    return auditLogService.findByDateRange(startDate, endDate, pageable);
  }

  /**
   * Retrieves audit logs filtered by source IP with pagination.
   *
   * @param sourceIp The source IP to filter by
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-ip")
  @Operation(
    summary = "Get audit logs by source IP",
    description = "Retrieves audit logs filtered by source IP address"
  )
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Page<AuditLogInfo> getBySourceIp(
    @Parameter(
      description = "Source IP address",
      required = true
    ) @RequestParam String sourceIp,
    Pageable pageable
  ) {
    return auditLogService.findBySourceIp(sourceIp, pageable);
  }

  /**
   * Retrieves audit logs with error status with pagination.
   *
   * @param pageable Pagination information
   * @return Page of error audit logs
   */
  @GetMapping("/errors")
  @Operation(
    summary = "Get error audit logs",
    description = "Retrieves audit logs with error status"
  )
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Page<AuditLogInfo> getErrors(Pageable pageable) {
    return auditLogService.findErrors(pageable);
  }

  /**
   * Retrieves audit logs for a specific entity within a date range.
   *
   * @param entityName The entity name to filter by
   * @param startDate The start date of the range
   * @param endDate The end date of the range
   * @param pageable Pagination information
   * @return Page of matching audit logs
   */
  @GetMapping("/by-entity-and-date")
  @Operation(
    summary = "Get audit logs by entity and date range",
    description = "Retrieves audit logs filtered by entity name and date range"
  )
  public Page<AuditLogInfo> getByEntityAndDate(
    @Parameter(
      description = "Entity name",
      required = true
    ) @RequestParam String entityName,
    @Parameter(
      description = "Start date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    Pageable pageable
  ) {
    return auditLogService.findByEntityNameAndTimestampBetween(
      entityName,
      startDate,
      endDate,
      pageable
    );
  }
}
