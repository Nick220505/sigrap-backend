package com.sigrap.audit.domain.model;

/**
 * Value object representing an audit log identifier.
 * Provides type safety and validation for audit log IDs.
 */
public record AuditLogId(Long value) {
    
    public AuditLogId {
        if (value == null) {
            throw new IllegalArgumentException("Audit log ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Audit log ID must be positive");
        }
    }
}
