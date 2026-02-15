package com.sigrap.audit.domain.event;

import java.time.LocalDateTime;

/**
 * Domain event representing a user logout.
 * This event is published when a user logs out of the system.
 */
public record UserLogoutEvent(
        String username,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent
) implements DomainEvent {
    
    /**
     * Creates a new user logout event.
     * 
     * @param username The username who logged out
     * @param occurredAt When the logout occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     */
    public UserLogoutEvent {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }
    
    @Override
    public String performedBy() {
        return username;
    }
}
