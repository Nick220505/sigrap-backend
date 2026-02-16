package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.employee.application.port.in.command.UpdateScheduleCommand;
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
 * Unit tests for UpdateScheduleService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class UpdateScheduleServiceTest {
    
    @Mock
    private ScheduleRepositoryPort scheduleRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private UpdateScheduleService updateScheduleService;
    
    @BeforeEach
    void setUp() {
        reset(scheduleRepository, eventPublisher);
    }
    
    @Test
    void shouldUpdateScheduleSuccessfully() {
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
        
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            "Tuesday",
            LocalTime.of(8, 0),
            LocalTime.of(16, 0),
            "Flexible",
            false
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Schedule result = updateScheduleService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Tuesday", result.getDay());
        assertEquals(LocalTime.of(8, 0), result.getStartTime());
        assertEquals(LocalTime.of(16, 0), result.getEndTime());
        assertEquals("Flexible", result.getType());
        assertFalse(result.isActive());
        
        verify(scheduleRepository).findById(id);
        verify(scheduleRepository).save(any(Schedule.class));
        verify(eventPublisher).publish(any());
    }
    
    @Test
    void shouldUpdateOnlyProvidedFields() {
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
        
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            "Wednesday",
            null,
            null,
            null,
            null
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Schedule result = updateScheduleService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Wednesday", result.getDay());
        assertEquals(LocalTime.of(9, 0), result.getStartTime()); // Unchanged
        assertEquals(LocalTime.of(17, 0), result.getEndTime()); // Unchanged
        assertEquals("Regular", result.getType()); // Unchanged
        assertTrue(result.isActive()); // Unchanged
        
        verify(scheduleRepository).save(any(Schedule.class));
    }
    
    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given
        ScheduleId id = new ScheduleId(999L);
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            "Monday",
            null,
            null,
            null,
            null
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateScheduleService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(scheduleRepository).findById(id);
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }
    
    @Test
    void shouldActivateSchedule() {
        // Given
        ScheduleId id = new ScheduleId(1L);
        Schedule existingSchedule = new Schedule(
            id,
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular",
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            null,
            null,
            null,
            null,
            true
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Schedule result = updateScheduleService.update(id, command);
        
        // Then
        assertTrue(result.isActive());
        verify(scheduleRepository).save(any(Schedule.class));
    }
    
    @Test
    void shouldDeactivateSchedule() {
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
        
        UpdateScheduleCommand command = new UpdateScheduleCommand(
            null,
            null,
            null,
            null,
            false
        );
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Schedule result = updateScheduleService.update(id, command);
        
        // Then
        assertFalse(result.isActive());
        verify(scheduleRepository).save(any(Schedule.class));
    }
}
