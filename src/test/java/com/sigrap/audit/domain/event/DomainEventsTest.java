package com.sigrap.audit.domain.event;

import com.sigrap.audit.domain.model.EntityType;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for audit domain events.
 */
class DomainEventsTest {
    
    @Test
    void shouldCreateEntityCreatedEvent() {
        // Given
        EntityType entityType = EntityType.PRODUCT;
        String entityId = "123";
        String performedBy = "user@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        EntityCreatedEvent event = new EntityCreatedEvent(
            entityType, entityId, performedBy, occurredAt,
            "192.168.1.1", "Mozilla/5.0", "Details", 100L
        );
        
        // Then
        assertEquals(entityType, event.entityType());
        assertEquals(entityId, event.entityId());
        assertEquals(performedBy, event.performedBy());
        assertEquals(occurredAt, event.occurredAt());
        assertEquals("192.168.1.1", event.sourceIp());
        assertEquals("Mozilla/5.0", event.userAgent());
        assertEquals("Details", event.details());
        assertEquals(100L, event.durationMs());
    }
    
    @Test
    void shouldThrowExceptionForNullEntityTypeInCreatedEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityCreatedEvent(
                null, "123", "user@example.com", LocalDateTime.now(),
                null, null, null, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForBlankEntityIdInCreatedEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityCreatedEvent(
                EntityType.PRODUCT, "   ", "user@example.com", LocalDateTime.now(),
                null, null, null, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForBlankPerformedByInCreatedEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new EntityCreatedEvent(
                EntityType.PRODUCT, "123", "", LocalDateTime.now(),
                null, null, null, null
            )
        );
    }
    
    @Test
    void shouldCreateEntityUpdatedEvent() {
        // Given
        EntityType entityType = EntityType.CATEGORY;
        String entityId = "456";
        String performedBy = "admin@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        String changes = "Name changed from 'Old' to 'New'";
        
        // When
        EntityUpdatedEvent event = new EntityUpdatedEvent(
            entityType, entityId, performedBy, occurredAt,
            "10.0.0.1", "Chrome", changes, 50L
        );
        
        // Then
        assertEquals(entityType, event.entityType());
        assertEquals(entityId, event.entityId());
        assertEquals(performedBy, event.performedBy());
        assertEquals(changes, event.changes());
    }
    
    @Test
    void shouldCreateEntityDeletedEvent() {
        // Given
        EntityType entityType = EntityType.CUSTOMER;
        String entityId = "789";
        String performedBy = "manager@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        EntityDeletedEvent event = new EntityDeletedEvent(
            entityType, entityId, performedBy, occurredAt,
            null, null, "Deleted due to inactivity", 75L
        );
        
        // Then
        assertEquals(entityType, event.entityType());
        assertEquals(entityId, event.entityId());
        assertEquals(performedBy, event.performedBy());
        assertEquals("Deleted due to inactivity", event.details());
    }
    
    @Test
    void shouldCreateEntityViewedEvent() {
        // Given
        EntityType entityType = EntityType.USER;
        String entityId = "101";
        String performedBy = "viewer@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        EntityViewedEvent event = new EntityViewedEvent(
            entityType, entityId, performedBy, occurredAt,
            "172.16.0.1", "Safari", 25L
        );
        
        // Then
        assertEquals(entityType, event.entityType());
        assertEquals(entityId, event.entityId());
        assertEquals(performedBy, event.performedBy());
    }
    
    @Test
    void shouldCreateBulkEntityDeletedEvent() {
        // Given
        EntityType entityType = EntityType.PRODUCT;
        List<String> entityIds = List.of("1", "2", "3", "4", "5");
        String performedBy = "admin@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        BulkEntityDeletedEvent event = new BulkEntityDeletedEvent(
            entityType, entityIds, performedBy, occurredAt,
            "192.168.1.100", "Firefox", 200L
        );
        
        // Then
        assertEquals(entityType, event.entityType());
        assertEquals(entityIds, event.entityIds());
        assertEquals(5, event.getDeletedCount());
        assertEquals(performedBy, event.performedBy());
    }
    
    @Test
    void shouldThrowExceptionForEmptyEntityIdsInBulkDeleteEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new BulkEntityDeletedEvent(
                EntityType.PRODUCT, List.of(), "user@example.com", LocalDateTime.now(),
                null, null, null
            )
        );
    }
    
    @Test
    void shouldCreateUserLoginEvent() {
        // Given
        String username = "user@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        UserLoginEvent event = new UserLoginEvent(
            username, occurredAt, "192.168.1.50", "Chrome", 150L
        );
        
        // Then
        assertEquals(username, event.username());
        assertEquals(username, event.performedBy());
        assertEquals(occurredAt, event.occurredAt());
    }
    
    @Test
    void shouldCreateUserLoginFailedEvent() {
        // Given
        String username = "hacker@example.com";
        String reason = "Invalid credentials";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        UserLoginFailedEvent event = new UserLoginFailedEvent(
            username, reason, occurredAt, "203.0.113.1", "Unknown", 100L
        );
        
        // Then
        assertEquals(username, event.username());
        assertEquals(username, event.performedBy());
        assertEquals(reason, event.reason());
    }
    
    @Test
    void shouldThrowExceptionForBlankReasonInLoginFailedEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new UserLoginFailedEvent(
                "user@example.com", "", LocalDateTime.now(),
                null, null, null
            )
        );
    }
    
    @Test
    void shouldCreateUserLogoutEvent() {
        // Given
        String username = "user@example.com";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        UserLogoutEvent event = new UserLogoutEvent(
            username, occurredAt, "192.168.1.50", "Chrome"
        );
        
        // Then
        assertEquals(username, event.username());
        assertEquals(username, event.performedBy());
        assertEquals(occurredAt, event.occurredAt());
    }
    
    @Test
    void shouldCreateAccessDeniedEvent() {
        // Given
        String username = "user@example.com";
        EntityType entityType = EntityType.SALE;
        String entityId = "999";
        String attemptedAction = "DELETE";
        String reason = "Insufficient permissions";
        LocalDateTime occurredAt = LocalDateTime.now();
        
        // When
        AccessDeniedEvent event = new AccessDeniedEvent(
            username, entityType, entityId, attemptedAction, reason, occurredAt,
            "192.168.1.75", "Firefox"
        );
        
        // Then
        assertEquals(username, event.username());
        assertEquals(username, event.performedBy());
        assertEquals(entityType, event.entityType());
        assertEquals(entityId, event.entityId());
        assertEquals(attemptedAction, event.attemptedAction());
        assertEquals(reason, event.reason());
    }
    
    @Test
    void shouldThrowExceptionForBlankAttemptedActionInAccessDeniedEvent() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            new AccessDeniedEvent(
                "user@example.com", EntityType.PRODUCT, "1", "",
                "No permission", LocalDateTime.now(), null, null
            )
        );
    }
    
    @Test
    void shouldAllowNullEntityTypeInAccessDeniedEvent() {
        // Given/When
        AccessDeniedEvent event = new AccessDeniedEvent(
            "user@example.com", null, null, "VIEW_REPORTS",
            "Not authorized", LocalDateTime.now(), null, null
        );
        
        // Then
        assertNull(event.entityType());
        assertNull(event.entityId());
    }
}
