package com.sigrap.audit.infrastructure.adapter.in.rest;

import com.sigrap.audit.domain.model.AuditAction;
import com.sigrap.audit.domain.model.AuditStatus;
import com.sigrap.audit.domain.model.EntityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for audit log REST API.
 * 
 * <p>This record represents the JSON response structure for audit log queries.
 * It is separate from the domain model to allow independent evolution of the
 * API contract and domain model.</p>
 */
@Schema(description = "Audit log response with complete audit event details")
public record AuditLogResponse(
    @Schema(
        description = "Unique identifier of the audit log entry",
        example = "1"
    )
    Long id,
    
    @Schema(
        description = "Username of the user who performed the action",
        example = "john.doe"
    )
    String username,
    
    @Schema(
        description = "Type of action performed (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.)",
        example = "CREATE"
    )
    AuditAction action,
    
    @Schema(
        description = "Type of entity affected by the action (PRODUCT, CATEGORY, USER, SALE, etc.)",
        example = "PRODUCT"
    )
    EntityType entityType,
    
    @Schema(
        description = "Unique identifier of the affected entity",
        example = "123"
    )
    String entityId,
    
    @Schema(
        description = "Timestamp when the action occurred",
        example = "2026-02-15T21:30:00"
    )
    LocalDateTime timestamp,
    
    @Schema(
        description = "IP address from which the action was performed",
        example = "192.168.1.100"
    )
    String sourceIp,
    
    @Schema(
        description = "User agent string of the client application",
        example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    )
    String userAgent,
    
    @Schema(
        description = "Additional details about the action in JSON format",
        example = "{\"previousValue\": \"old\", \"newValue\": \"new\"}"
    )
    String details,
    
    @Schema(
        description = "Status of the audit event (SUCCESS, FAILURE, PENDING)",
        example = "SUCCESS"
    )
    AuditStatus status,
    
    @Schema(
        description = "Duration of the operation in milliseconds",
        example = "150"
    )
    Long durationMs
) {}
