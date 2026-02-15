package com.sigrap.audit.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AuditLog domain entity.
 * These are pure unit tests with no Spring context or framework dependencies.
 */
class AuditLogTest {
    
    @Test
    void shouldCreateAuditLogWithValidData() {
        // Given
        String username = "test@example.com";
        AuditAction action = AuditAction.CREATE;
        EntityType entityType = EntityType.PRODUCT;
        String entityId = "123";
        LocalDateTime timestamp = LocalDateTime.now();
        AuditStatus status = AuditStatus.SUCCESS;
        
        // When
        AuditLog auditLog = new AuditLog(
            username, action, entityType, entityId, timestamp,
            "192.168.1.1", "Mozilla/5.0", "Details", status, 100L
        );
        
        // Then
        assertNull(auditLog.getId());
        assertEquals(username, auditLog.getUsername());
        assertEquals(action, auditLog.getAction());
        assertEquals(entityType, auditLog.getEntityType());
        assertEquals(entityId, auditLog.getEntityId());
        assertEquals(timestamp, auditLog.getTimestamp());
        assertEquals("192.168.1.1", auditLog.getSourceIp());
        assertEquals("Mozilla/5.0", auditLog.getUserAgent());
        assertEquals("Details", auditLog.getDetails());
        assertEquals(status, auditLog.getStatus());
        assertEquals(100L, auditLog.getDurationMs());
    }
    
    @Test
    void shouldCreateAuditLogWithId() {
        // Given
        AuditLogId id = new AuditLogId(1L);
        String username = "test@example.com";
        AuditAction action = AuditAction.UPDATE;
        EntityType entityType = EntityType.CATEGORY;
        LocalDateTime timestamp = LocalDateTime.now();
        AuditStatus status = AuditStatus.SUCCESS;
        
        // When
        AuditLog auditLog = new AuditLog(
            id, username, action, entityType, "456", timestamp,
            null, null, null, status, null
        );
        
        // Then
        assertEquals(id, auditLog.getId());
        assertFalse(auditLog.isNew());
    }
    
    @Test
    void shouldIdentifyNewAuditLog() {
        // Given
        AuditLog auditLog = new AuditLog(
            "user@example.com", AuditAction.DELETE, EntityType.USER, "789",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        
        // When/Then
        assertTrue(auditLog.isNew());
    }
    
    @Test
    void shouldIdentifySuccessfulAuditLog() {
        // Given
        AuditLog auditLog = new AuditLog(
            "user@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        
        // When/Then
        assertTrue(auditLog.isSuccessful());
        assertFalse(auditLog.isFailed());
    }
    
    @Test
    void shouldIdentifyFailedAuditLog() {
        // Given
        AuditLog auditLog = new AuditLog(
            "user@example.com", AuditAction.UPDATE, EntityType.CUSTOMER, "2",
            LocalDateTime.now(), null, null, "Error occurred", AuditStatus.ERROR, null
        );
        
        // When/Then
        assertTrue(auditLog.isFailed());
        assertFalse(auditLog.isSuccessful());
    }
    
    @Test
    void shouldThrowExceptionForNullUsername() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            new AuditLog(
                null, AuditAction.CREATE, EntityType.PRODUCT, "1",
                LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForNullAction() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            new AuditLog(
                "user@example.com", null, EntityType.PRODUCT, "1",
                LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForNullEntityType() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            new AuditLog(
                "user@example.com", AuditAction.CREATE, null, "1",
                LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForNullTimestamp() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            new AuditLog(
                "user@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
                null, null, null, null, AuditStatus.SUCCESS, null
            )
        );
    }
    
    @Test
    void shouldThrowExceptionForNullStatus() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            new AuditLog(
                "user@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
                LocalDateTime.now(), null, null, null, null, null
            )
        );
    }
    
    @Test
    void shouldAllowNullEntityId() {
        // Given/When
        AuditLog auditLog = new AuditLog(
            "user@example.com", AuditAction.LOGIN, EntityType.USER, null,
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        
        // Then
        assertNull(auditLog.getEntityId());
    }
    
    @Test
    void shouldImplementEqualsBasedOnId() {
        // Given
        AuditLogId id = new AuditLogId(1L);
        AuditLog log1 = new AuditLog(
            id, "user1@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        AuditLog log2 = new AuditLog(
            id, "user2@example.com", AuditAction.UPDATE, EntityType.CATEGORY, "2",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        
        // When/Then
        assertEquals(log1, log2);
        assertEquals(log1.hashCode(), log2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWithDifferentIds() {
        // Given
        AuditLog log1 = new AuditLog(
            new AuditLogId(1L), "user@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        AuditLog log2 = new AuditLog(
            new AuditLogId(2L), "user@example.com", AuditAction.CREATE, EntityType.PRODUCT, "1",
            LocalDateTime.now(), null, null, null, AuditStatus.SUCCESS, null
        );
        
        // When/Then
        assertNotEquals(log1, log2);
    }
}
