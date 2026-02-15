package com.sigrap.audit.application.service;

import com.sigrap.audit.domain.event.*;
import com.sigrap.audit.domain.model.*;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateAuditLogService.
 * Tests the application layer logic with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class CreateAuditLogServiceTest {
    
    @Mock
    private AuditLogRepositoryPort auditLogRepository;
    
    @InjectMocks
    private CreateAuditLogService createAuditLogService;
    
    @Test
    void shouldCreateAuditLogFromEntityCreatedEvent() {
        // Given
        EntityCreatedEvent event = new EntityCreatedEvent(
                EntityType.CATEGORY,
                "123",
                "testuser",
                LocalDateTime.now(),
                "192.168.1.1",
                "Mozilla/5.0",
                "Created new category",
                100L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(1L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(AuditAction.CREATE, result.getAction());
        assertEquals(EntityType.CATEGORY, result.getEntityType());
        assertEquals("123", result.getEntityId());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        assertEquals("192.168.1.1", result.getSourceIp());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        assertEquals("Created new category", result.getDetails());
        assertEquals(100L, result.getDurationMs());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromEntityUpdatedEvent() {
        // Given
        EntityUpdatedEvent event = new EntityUpdatedEvent(
                EntityType.PRODUCT,
                "456",
                "admin",
                LocalDateTime.now(),
                "10.0.0.1",
                "Chrome",
                "Updated price",
                50L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(2L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(AuditAction.UPDATE, result.getAction());
        assertEquals(EntityType.PRODUCT, result.getEntityType());
        assertEquals("456", result.getEntityId());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        assertEquals("Updated price", result.getDetails());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromEntityDeletedEvent() {
        // Given
        EntityDeletedEvent event = new EntityDeletedEvent(
                EntityType.CUSTOMER,
                "789",
                "manager",
                LocalDateTime.now(),
                "172.16.0.1",
                "Safari",
                "Deleted inactive customer",
                75L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(3L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("manager", result.getUsername());
        assertEquals(AuditAction.DELETE, result.getAction());
        assertEquals(EntityType.CUSTOMER, result.getEntityType());
        assertEquals("789", result.getEntityId());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromBulkEntityDeletedEvent() {
        // Given
        BulkEntityDeletedEvent event = new BulkEntityDeletedEvent(
                EntityType.PRODUCT,
                List.of("1", "2", "3"),
                "admin",
                LocalDateTime.now(),
                "192.168.1.1",
                "Firefox",
                200L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(4L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(AuditAction.BULK_DELETE, result.getAction());
        assertEquals(EntityType.PRODUCT, result.getEntityType());
        assertEquals("1,2,3", result.getEntityId());
        assertEquals("Deleted 3 entities", result.getDetails());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromEntityViewedEvent() {
        // Given
        EntityViewedEvent event = new EntityViewedEvent(
                EntityType.CUSTOMER,
                "999",
                "viewer",
                LocalDateTime.now(),
                "10.10.10.10",
                "Edge",
                25L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(5L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("viewer", result.getUsername());
        assertEquals(AuditAction.VIEW, result.getAction());
        assertEquals(EntityType.CUSTOMER, result.getEntityType());
        assertEquals("999", result.getEntityId());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromUserLoginEvent() {
        // Given
        UserLoginEvent event = new UserLoginEvent(
                "john.doe",
                LocalDateTime.now(),
                "192.168.1.100",
                "Chrome/91.0",
                150L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(6L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals(AuditAction.LOGIN, result.getAction());
        assertEquals(EntityType.USER, result.getEntityType());
        assertEquals("john.doe", result.getEntityId());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromUserLoginFailedEvent() {
        // Given
        UserLoginFailedEvent event = new UserLoginFailedEvent(
                "hacker",
                "Invalid credentials",
                LocalDateTime.now(),
                "203.0.113.1",
                "curl/7.68.0",
                100L
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(7L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("hacker", result.getUsername());
        assertEquals(AuditAction.LOGIN, result.getAction());
        assertEquals(EntityType.USER, result.getEntityType());
        assertEquals(AuditStatus.ERROR, result.getStatus());
        assertTrue(result.getDetails().contains("Invalid credentials"));
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromUserLogoutEvent() {
        // Given
        UserLogoutEvent event = new UserLogoutEvent(
                "jane.smith",
                LocalDateTime.now(),
                "192.168.1.50",
                "Firefox/89.0"
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(8L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        assertEquals(AuditAction.LOGOUT, result.getAction());
        assertEquals(EntityType.USER, result.getEntityType());
        assertEquals(AuditStatus.SUCCESS, result.getStatus());
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldCreateAuditLogFromAccessDeniedEvent() {
        // Given
        AccessDeniedEvent event = new AccessDeniedEvent(
                "unauthorized-user",
                EntityType.PRODUCT,
                "secret-123",
                "DELETE",
                "Insufficient permissions",
                LocalDateTime.now(),
                "198.51.100.1",
                "PostmanRuntime/7.28.0"
        );
        
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            return new AuditLog(
                    new AuditLogId(9L),
                    log.getUsername(),
                    log.getAction(),
                    log.getEntityType(),
                    log.getEntityId(),
                    log.getTimestamp(),
                    log.getSourceIp(),
                    log.getUserAgent(),
                    log.getDetails(),
                    log.getStatus(),
                    log.getDurationMs()
            );
        });
        
        // When
        AuditLog result = createAuditLogService.createFromEvent(event);
        
        // Then
        assertNotNull(result);
        assertEquals("unauthorized-user", result.getUsername());
        assertEquals(AuditAction.ACCESS_DENIED, result.getAction());
        assertEquals(EntityType.PRODUCT, result.getEntityType());
        assertEquals("secret-123", result.getEntityId());
        assertEquals(AuditStatus.ERROR, result.getStatus());
        assertTrue(result.getDetails().contains("Insufficient permissions"));
        assertTrue(result.getDetails().contains("DELETE"));
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void shouldThrowExceptionForNullEvent() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
                createAuditLogService.createFromEvent(null));
        
        verify(auditLogRepository, never()).save(any());
    }
}
