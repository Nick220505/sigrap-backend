package com.sigrap.audit.infrastructure.adapter.in.rest;

import com.sigrap.audit.application.port.in.GetAuditLogUseCase;
import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.AuditLogId;
import com.sigrap.audit.domain.model.EntityType;
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
    public ResponseEntity<AuditLogResponse> getById(@PathVariable Long id) {
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
    public ResponseEntity<List<AuditLogResponse>> getByUsername(@PathVariable String username) {
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
    public ResponseEntity<List<AuditLogResponse>> getByEntity(
            @PathVariable EntityType entityType,
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
    public ResponseEntity<List<AuditLogResponse>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
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
    public ResponseEntity<List<AuditLogResponse>> getByUsernameAndTimeRange(
            @PathVariable String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
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
