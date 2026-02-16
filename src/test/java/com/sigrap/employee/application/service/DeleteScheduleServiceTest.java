package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.employee.domain.port.ScheduleRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteScheduleService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class DeleteScheduleServiceTest {
    
    @Mock
    private ScheduleRepositoryPort scheduleRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private DeleteScheduleService deleteScheduleService;
    
    @BeforeEach
    void setUp() {
        reset(scheduleRepository, eventPublisher);
    }
    
    @Test
    void shouldDeleteScheduleSuccessfully() {
        // Given
        ScheduleId id = new ScheduleId(1L);
        Schedule existingSchedule = new Schedule(
            id,
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular",
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));
        doNothing().when(scheduleRepository).deleteById(id);
        
        // When
        deleteScheduleService.delete(id);
        
        // Then
        verify(scheduleRepository).findById(id);
        verify(scheduleRepository).deleteById(id);
        verify(eventPublisher).publish(any());
    }
    
    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given
        ScheduleId id = new ScheduleId(999L);
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deleteScheduleService.delete(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(scheduleRepository).findById(id);
        verify(scheduleRepository, never()).deleteById(any());
        verifyNoInteractions(eventPublisher);
    }
}
