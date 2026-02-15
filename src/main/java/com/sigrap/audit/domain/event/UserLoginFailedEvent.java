package com.sigrap.audit.domain.event;

import java.time.LocalDateTime;

/**
 * Domain event representing a failed user login attempt.
 * This event is published when a user fails to authenticate.
 * Important for security monitoring and detecting potential attacks.
 */
public record UserLoginFailedEvent(
        String username,
        String reason,
        LocalDateTime occurredAt,
        String sourceIp,
        String userAgent,
        Long durationMs
) implements DomainEvent {
    
    /**
     * Creates a new user login failed event.
     * 
     * @param username The username that attempted to log in
     * @param reason The reason for the failure (e.g., "Invalid credentials", "Account locked")
     * @param occurredAt When the failed login occurred
     * @param sourceIp The IP address of the client (may be null)
     * @param userAgent The user agent of the client (may be null)
     * @param durationMs The duration of the login attempt in milliseconds (may be null)
     */
    public UserLoginFailedEvent {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be blank");
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
