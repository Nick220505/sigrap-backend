package com.sigrap.audit.domain.model;

/**
 * Value object representing an audit action type.
 * Defines the standard actions that can be audited in the system.
 */
public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,
    ACCESS_DENIED,
    EXPORT,
    IMPORT,
    BULK_DELETE,
    RESTORE,
    ARCHIVE,
    ACTIVATE,
    DEACTIVATE;
    
    /**
     * Gets the action name as a string.
     * 
     * @return The action name
     */
    public String getValue() {
        return this.name();
    }
}
