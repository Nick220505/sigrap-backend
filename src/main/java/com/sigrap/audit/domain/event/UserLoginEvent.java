package com.sigrap.audit.domain.event;

import java.time.LocalDateTime;

/**
 * Domain event representing a successful user login.
 * This event is published when a user successfully authenticates.
 */
public record UserLoginEvent(
        String username,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new user login event.
     * 
     * @param username The username who logged in
     * @param occurredAt When the login occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param durationMs The duration of the login operation in milliseconds (may be null)
     */
    public UserLoginEvent {
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
