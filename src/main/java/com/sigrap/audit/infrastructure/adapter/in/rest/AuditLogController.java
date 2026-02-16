package com.sigrap.audit.infrastructure.adapter.in.rest;

import com.sigrap.audit.application.port.in.GetAuditLogUseCase;
import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for audit log queries.
 * 
 * <p>This input adapter translates HTTP requests into use case calls and
 * returns HTTP responses. It provides endpoints for querying audit logs
 * by various criteria.</p>
 * 
 * <p>All endpoints require AUDIT_VIEW permission to access.</p>
 */
@RestController
@RequestMapping("/api/v2/audit-logs")
@Slf4j
@PreAuthorize("hasAuthority('AUDIT_VIEW')")
@Tag(name = "Audit Logs", description = "APIs for viewing system audit logs")
public class AuditLogController {
    
    private final GetAuditLogUseCase getAuditLogUseCase;
    private final AuditLogResponseMapper responseMapper;
    
    public AuditLogController(
            GetAuditLogUseCase getAuditLogUseCase,
            AuditLogResponseMapper responseMapper) {
        this.getAuditLogUseCase = getAuditLogUseCase;
        this.responseMapper = responseMapper;
    }
    
    /**
     * Gets an audit log by ID.
     * 
     * @param id The audit log ID
     * @return The audit log response
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get audit log by ID",
        description = "Retrieves a specific audit log entry by its unique identifier. Returns detailed information " +
                      "about a single audit event including user, action, entity, timestamp, and status. " +
                      "Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Audit log found and retrieved successfully",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Audit log not found with the specified ID"
    )
    public ResponseEntity<AuditLogResponse> getById(
            @Parameter(
                description = "Audit log unique identifier",
                required = true,
                example = "1"
            )
            @PathVariable Long id) {
        log.debug("GET /api/v2/audit-logs/{}", id);
        AuditLogId auditLogId = new AuditLogId(id);
        AuditLog auditLog = getAuditLogUseCase.getById(auditLogId);
        return ResponseEntity.ok(responseMapper.toResponse(auditLog));
    }
    
    /**
     * Gets all audit logs.
     * 
     * @return List of audit log responses
     */
    @GetMapping
    @Operation(
        summary = "Get all audit logs",
        description = "Retrieves all audit logs from the system. This endpoint returns a complete list of all audit events " +
                      "including user actions, entity changes, and system events. Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of audit logs retrieved successfully",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    public ResponseEntity<List<AuditLogResponse>> getAll() {
        log.debug("GET /api/v2/audit-logs");
        List<AuditLog> auditLogs = getAuditLogUseCase.getAll();
        List<AuditLogResponse> responses = auditLogs.stream()
                .map(responseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Gets audit logs by username.
     * 
     * @param username The username to search for
     * @return List of audit log responses
     */
    @GetMapping("/by-username/{username}")
    @Operation(
        summary = "Get audit logs by username",
        description = "Retrieves all audit logs for a specific user. Returns all audit events performed by the " +
                      "specified username. Useful for tracking user activity and compliance auditing. " +
                      "Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of audit logs for the specified user",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    public ResponseEntity<List<AuditLogResponse>> getByUsername(
            @Parameter(
                description = "Username to filter audit logs",
                required = true,
                example = "john.doe"
            )
            @PathVariable String username) {
        log.debug("GET /api/v2/audit-logs/by-username/{}", username);
        List<AuditLog> auditLogs = getAuditLogUseCase.getByUsername(username);
        List<AuditLogResponse> responses = auditLogs.stream()
                .map(responseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Gets audit logs by entity.
     * 
     * @param entityType The entity type
     * @param entityId The entity ID
     * @return List of audit log responses
     */
    @GetMapping("/by-entity/{entityType}/{entityId}")
    @Operation(
        summary = "Get audit logs by entity",
        description = "Retrieves all audit logs for a specific entity. Returns all audit events related to a " +
                      "particular entity type and ID. Useful for tracking changes to specific resources. " +
                      "Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of audit logs for the specified entity",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    public ResponseEntity<List<AuditLogResponse>> getByEntity(
            @Parameter(
                description = "Type of entity (e.g., PRODUCT, CATEGORY, USER, SALE)",
                required = true,
                example = "PRODUCT"
            )
            @PathVariable EntityType entityType,
            @Parameter(
                description = "Unique identifier of the entity",
                required = true,
                example = "123"
            )
            @PathVariable String entityId) {
        log.debug("GET /api/v2/audit-logs/by-entity/{}/{}", entityType, entityId);
        List<AuditLog> auditLogs = getAuditLogUseCase.getByEntity(entityType, entityId);
        List<AuditLogResponse> responses = auditLogs.stream()
                .map(responseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Gets audit logs within a time range.
     * 
     * @param startTime The start of the time range (ISO format)
     * @param endTime The end of the time range (ISO format)
     * @return List of audit log responses
     */
    @GetMapping("/by-time-range")
    @Operation(
        summary = "Get audit logs by time range",
        description = "Retrieves all audit logs within a specified time range. Returns audit events that occurred " +
                      "between the start and end timestamps. Useful for generating audit reports for specific periods. " +
                      "Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of audit logs within the specified time range",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - invalid date format or end time before start time"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    public ResponseEntity<List<AuditLogResponse>> getByTimeRange(
            @Parameter(
                description = "Start of time range in ISO 8601 format",
                required = true,
                example = "2026-01-01T00:00:00"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(
                description = "End of time range in ISO 8601 format",
                required = true,
                example = "2026-12-31T23:59:59"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("GET /api/v2/audit-logs/by-time-range?startTime={}&endTime={}", startTime, endTime);
        List<AuditLog> auditLogs = getAuditLogUseCase.getByTimeRange(startTime, endTime);
        List<AuditLogResponse> responses = auditLogs.stream()
                .map(responseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Gets audit logs by username within a time range.
     * 
     * @param username The username to search for
     * @param startTime The start of the time range (ISO format)
     * @param endTime The end of the time range (ISO format)
     * @return List of audit log responses
     */
    @GetMapping("/by-username-and-time-range/{username}")
    @Operation(
        summary = "Get audit logs by username and time range",
        description = "Retrieves all audit logs for a specific user within a time range. Combines username filtering " +
                      "with time-based filtering for detailed user activity tracking. Useful for compliance audits " +
                      "and user behavior analysis. Requires AUDIT_VIEW permission."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of audit logs for the specified user and time range",
        content = @Content(schema = @Schema(implementation = AuditLogResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - invalid date format or end time before start time"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - authentication required"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - AUDIT_VIEW permission required"
    )
    public ResponseEntity<List<AuditLogResponse>> getByUsernameAndTimeRange(
            @Parameter(
                description = "Username to filter audit logs",
                required = true,
                example = "john.doe"
            )
            @PathVariable String username,
            @Parameter(
                description = "Start of time range in ISO 8601 format",
                required = true,
                example = "2026-01-01T00:00:00"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(
                description = "End of time range in ISO 8601 format",
                required = true,
                example = "2026-12-31T23:59:59"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("GET /api/v2/audit-logs/by-username-and-time-range/{}?startTime={}&endTime={}", 
                username, startTime, endTime);
        List<AuditLog> auditLogs = getAuditLogUseCase.getByUsernameAndTimeRange(username, startTime, endTime);
        List<AuditLogResponse> responses = auditLogs.stream()
                .map(responseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
