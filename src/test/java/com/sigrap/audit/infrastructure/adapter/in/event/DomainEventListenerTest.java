package com.sigrap.audit.infrastructure.adapter.in.event;

import com.sigrap.audit.application.port.in.CreateAuditLogUseCase;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.AuditLog;
import com.sigrap.audit.domain.model.EntityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for DomainEventListener.
 * Tests the event listener with mocked use case.
 */
@ExtendWith(MockitoExtension.class)
class DomainEventListenerTest {
    
    @Mock
    private CreateAuditLogUseCase createAuditLogUseCase;
    
    @InjectMocks
    private DomainEventListener eventListener;
    
    @Test
    void shouldHandleDomainEvent() {
        // Given
        EntityCreatedEvent event = new EntityCreatedEvent(
            EntityType.CATEGORY,
            "123",
            "testuser",
            LocalDateTime.now(),
            null,
            null,
            null,
            null
        );
        
        when(createAuditLogUseCase.createFromEvent(any())).thenReturn(mock(AuditLog.class));
        
        // When
        eventListener.handleDomainEvent(event);
        
        // Then
        verify(createAuditLogUseCase, times(1)).createFromEvent(event);
    }
    
    @Test
    void shouldHandleExceptionGracefully() {
        // Given
        EntityCreatedEvent event = new EntityCreatedEvent(
            EntityType.PRODUCT,
            "456",
            "testuser",
            LocalDateTime.now(),
            null,
            null,
            null,
            null
        );
        
        when(createAuditLogUseCase.createFromEvent(any()))
            .thenThrow(new RuntimeException("Test exception"));
        
        // When/Then - should not throw exception
        eventListener.handleDomainEvent(event);
        
        verify(createAuditLogUseCase, times(1)).createFromEvent(event);
    }
}
