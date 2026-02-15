package com.sigrap.audit.infrastructure.adapter.in.rest;

import com.sigrap.audit.domain.model.AuditAction;
import com.sigrap.audit.domain.model.AuditStatus;
import com.sigrap.audit.domain.model.EntityType;

import java.time.LocalDateTime;

/**
 * Response DTO for audit log REST API.
 * 
 * <p>This record represents the JSON response structure for audit log queries.
 * It is separate from the domain model to allow independent evolution of the
 * API contract and domain model.</p>
 */
public record AuditLogResponse(
    Long id,
    String username,
    AuditAction action,
    EntityType entityType,
    String entityId,
    LocalDateTime timestamp,
    String sourceIp,
    String userAgent,
    String details,
    AuditStatus status,
    Long durationMs
) {}
