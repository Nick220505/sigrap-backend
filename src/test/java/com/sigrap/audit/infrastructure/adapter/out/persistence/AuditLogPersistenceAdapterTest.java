package com.sigrap.audit.infrastructure.adapter.out.persistence;

import com.sigrap.audit.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for AuditLogPersistenceAdapter.
 * Tests the persistence adapter with an in-memory database.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditLogPersistenceAdapterTest {
    
    @Autowired
    private AuditLogPersistenceAdapter adapter;
    
    @Test
    void shouldSaveAndRetrieveAuditLog() {
        // Given
        AuditLog auditLog = new AuditLog(
            "testuser",
            AuditAction.CREATE,
            EntityType.CATEGORY,
            "123",
            LocalDateTime.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            "Created category",
            AuditStatus.SUCCESS,
            100L
        );
        
        // When
        AuditLog saved = adapter.save(auditLog);
        
        // Then
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
        assertEquals(AuditAction.CREATE, saved.getAction());
        assertEquals(EntityType.CATEGORY, saved.getEntityType());
        assertEquals("123", saved.getEntityId());
        
        // Verify retrieval
        Optional<AuditLog> retrieved = adapter.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getUsername(), retrieved.get().getUsername());
    }
    
    @Test
    void shouldFindByUsername() {
        // Given
        AuditLog log1 = new AuditLog(
            "user1",
            AuditAction.CREATE,
            EntityType.PRODUCT,
            "1",
            LocalDateTime.now(),
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        );
        AuditLog log2 = new AuditLog(
            "user1",
            AuditAction.UPDATE,
            EntityType.PRODUCT,
            "1",
            LocalDateTime.now(),
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        );
        adapter.save(log1);
        adapter.save(log2);
        
        // When
        List<AuditLog> results = adapter.findByUsername("user1");
        
        // Then
        assertEquals(2, results.size());
    }
    
    @Test
    void shouldFindByEntity() {
        // Given
        AuditLog log = new AuditLog(
            "testuser",
            AuditAction.VIEW,
            EntityType.CUSTOMER,
            "456",
            LocalDateTime.now(),
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        );
        adapter.save(log);
        
        // When
        List<AuditLog> results = adapter.findByEntity(EntityType.CUSTOMER, "456");
        
        // Then
        assertFalse(results.isEmpty());
        assertEquals(EntityType.CUSTOMER, results.get(0).getEntityType());
        assertEquals("456", results.get(0).getEntityId());
    }
    
    @Test
    void shouldFindByTimestampBetween() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusHours(1);
        LocalDateTime end = now.plusHours(1);
        
        AuditLog log = new AuditLog(
            "testuser",
            AuditAction.DELETE,
            EntityType.SUPPLIER,
            "789",
            now,
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        );
        adapter.save(log);
        
        // When
        List<AuditLog> results = adapter.findByTimestampBetween(start, end);
        
        // Then
        assertFalse(results.isEmpty());
    }
    
    @Test
    void shouldCountByUsername() {
        // Given
        adapter.save(new AuditLog(
            "user2",
            AuditAction.CREATE,
            EntityType.SALE,
            "1",
            LocalDateTime.now(),
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        ));
        adapter.save(new AuditLog(
            "user2",
            AuditAction.UPDATE,
            EntityType.SALE,
            "1",
            LocalDateTime.now(),
            null,
            null,
            null,
            AuditStatus.SUCCESS,
            null
        ));
        
        // When
        long count = adapter.countByUsername("user2");
        
        // Then
        assertEquals(2, count);
    }
}
