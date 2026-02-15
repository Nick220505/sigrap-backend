package com.sigrap.audit.infrastructure.adapter.out.event;

import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test for SpringEventPublisherAdapter.
 * Tests the event publisher adapter with mocked Spring ApplicationEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class SpringEventPublisherAdapterTest {
    
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    
    @InjectMocks
    private SpringEventPublisherAdapter eventPublisher;
    
    @Test
    void shouldPublishEvent() {
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
        
        // When
        eventPublisher.publish(event);
        
        // Then
        verify(applicationEventPublisher, times(1)).publishEvent(event);
    }
    
    @Test
    void shouldThrowExceptionForNullEvent() {
        // When/Then
        assertThrows(NullPointerException.class, () -> 
            eventPublisher.publish(null)
        );
    }
}
