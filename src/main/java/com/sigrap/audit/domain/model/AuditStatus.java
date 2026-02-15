package com.sigrap.audit.domain.model;

/**
 * Value object representing the status of an audited action.
 */
public enum AuditStatus {
    SUCCESS,
    ERROR,
    PARTIAL_SUCCESS,
    PENDING;
    
    /**
     * Gets the status value as a string.
     * 
     * @return The status value
     */
    public String getValue() {
        return this.name();
    }
}
