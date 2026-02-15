package com.sigrap.audit.application.service;

import com.sigrap.audit.domain.model.*;
import com.sigrap.audit.domain.port.AuditLogRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetAuditLogService.
 * Tests the application layer logic with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class GetAuditLogServiceTest {
    
    @Mock
    private AuditLogRepositoryPort auditLogRepository;
    
    @InjectMocks
    private GetAuditLogService getAuditLogService;
    
    @Test
    void shouldGetAuditLogById() {
        // Given
        AuditLogId id = new AuditLogId(1L);
        AuditLog auditLog = createSampleAuditLog(id);
        
        when(auditLogRepository.findById(id)).thenReturn(Optional.of(auditLog));
        
        // When
        AuditLog result = getAuditLogService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(auditLogRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenAuditLogNotFound() {
        // Given
        AuditLogId id = new AuditLogId(999L);
        when(auditLogRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getById(id));
        
        verify(auditLogRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionForNullId() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
                getAuditLogService.getById(null));
        
        verify(auditLogRepository, never()).findById(any());
    }
    
    @Test
    void shouldGetAllAuditLogs() {
        // Given
        List<AuditLog> auditLogs = List.of(
                createSampleAuditLog(new AuditLogId(1L)),
                createSampleAuditLog(new AuditLogId(2L)),
                createSampleAuditLog(new AuditLogId(3L))
        );
        
        when(auditLogRepository.findAll()).thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = getAuditLogService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(auditLogRepository).findAll();
    }
    
    @Test
    void shouldGetAuditLogsByUsername() {
        // Given
        String username = "testuser";
        List<AuditLog> auditLogs = List.of(
                createSampleAuditLog(new AuditLogId(1L)),
                createSampleAuditLog(new AuditLogId(2L))
        );
        
        when(auditLogRepository.findByUsername(username)).thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = getAuditLogService.getByUsername(username);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogRepository).findByUsername(username);
    }
    
    @Test
    void shouldThrowExceptionForNullUsername() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByUsername(null));
        
        verify(auditLogRepository, never()).findByUsername(any());
    }
    
    @Test
    void shouldThrowExceptionForBlankUsername() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByUsername("  "));
        
        verify(auditLogRepository, never()).findByUsername(any());
    }
    
    @Test
    void shouldGetAuditLogsByEntity() {
        // Given
        EntityType entityType = EntityType.CATEGORY;
        String entityId = "123";
        List<AuditLog> auditLogs = List.of(
                createSampleAuditLog(new AuditLogId(1L))
        );
        
        when(auditLogRepository.findByEntity(entityType, entityId)).thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = getAuditLogService.getByEntity(entityType, entityId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByEntity(entityType, entityId);
    }
    
    @Test
    void shouldThrowExceptionForNullEntityType() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
                getAuditLogService.getByEntity(null, "123"));
        
        verify(auditLogRepository, never()).findByEntity(any(), any());
    }
    
    @Test
    void shouldThrowExceptionForNullEntityId() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByEntity(EntityType.CATEGORY, null));
        
        verify(auditLogRepository, never()).findByEntity(any(), any());
    }
    
    @Test
    void shouldGetAuditLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<AuditLog> auditLogs = List.of(
                createSampleAuditLog(new AuditLogId(1L)),
                createSampleAuditLog(new AuditLogId(2L))
        );
        
        when(auditLogRepository.findByTimestampBetween(startTime, endTime))
                .thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = getAuditLogService.getByTimeRange(startTime, endTime);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogRepository).findByTimestampBetween(startTime, endTime);
    }
    
    @Test
    void shouldThrowExceptionForNullStartTime() {
        // Given
        LocalDateTime endTime = LocalDateTime.now();
        
        // When/Then
        assertThrows(NullPointerException.class, () -> 
                getAuditLogService.getByTimeRange(null, endTime));
        
        verify(auditLogRepository, never()).findByTimestampBetween(any(), any());
    }
    
    @Test
    void shouldThrowExceptionForNullEndTime() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        
        // When/Then
        assertThrows(NullPointerException.class, () -> 
                getAuditLogService.getByTimeRange(startTime, null));
        
        verify(auditLogRepository, never()).findByTimestampBetween(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenStartTimeIsAfterEndTime() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByTimeRange(startTime, endTime));
        
        verify(auditLogRepository, never()).findByTimestampBetween(any(), any());
    }
    
    @Test
    void shouldGetAuditLogsByUsernameAndTimeRange() {
        // Given
        String username = "testuser";
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<AuditLog> auditLogs = List.of(
                createSampleAuditLog(new AuditLogId(1L))
        );
        
        when(auditLogRepository.findByUsernameAndTimestampBetween(username, startTime, endTime))
                .thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = getAuditLogService.getByUsernameAndTimeRange(
                username, startTime, endTime);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByUsernameAndTimestampBetween(username, startTime, endTime);
    }
    
    @Test
    void shouldThrowExceptionForNullUsernameInTimeRangeQuery() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByUsernameAndTimeRange(null, startTime, endTime));
        
        verify(auditLogRepository, never()).findByUsernameAndTimestampBetween(any(), any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenStartTimeIsAfterEndTimeInUsernameQuery() {
        // Given
        String username = "testuser";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
                getAuditLogService.getByUsernameAndTimeRange(username, startTime, endTime));
        
        verify(auditLogRepository, never()).findByUsernameAndTimestampBetween(any(), any(), any());
    }
    
    private AuditLog createSampleAuditLog(AuditLogId id) {
        return new AuditLog(
                id,
                "testuser",
                AuditAction.CREATE,
                EntityType.CATEGORY,
                "123",
                LocalDateTime.now(),
                "192.168.1.1",
                "Mozilla/5.0",
                "Test audit log",
                AuditStatus.SUCCESS,
                100L
        );
    }
}
