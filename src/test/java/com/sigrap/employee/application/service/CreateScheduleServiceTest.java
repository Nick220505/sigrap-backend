package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.employee.application.port.in.command.CreateScheduleCommand;
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

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateScheduleService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class CreateScheduleServiceTest {
    
    @Mock
    private ScheduleRepositoryPort scheduleRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private CreateScheduleService createScheduleService;
    
    @BeforeEach
    void setUp() {
        reset(scheduleRepository, eventPublisher);
    }
    
    @Test
    void shouldCreateScheduleSuccessfully() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            1L,
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule sch = invocation.getArgument(0);
            return new Schedule(
                new ScheduleId(1L),
                sch.getUserId(),
                sch.getDay(),
                sch.getStartTime(),
                sch.getEndTime(),
                sch.getType(),
                sch.isActive(),
                sch.getCreatedAt(),
                sch.getUpdatedAt()
            );
        });
        
        // When
        Schedule result = createScheduleService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(new UserId(1L), result.getUserId());
        assertEquals("Monday", result.getDay());
        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(17, 0), result.getEndTime());
        assertEquals("Regular", result.getType());
        assertTrue(result.isActive());
        
        verify(scheduleRepository).save(any(Schedule.class));
        verify(eventPublisher).publish(any());
    }
    
    @Test
    void shouldCreateScheduleWithNullType() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            1L,
            "Tuesday",
            LocalTime.of(8, 0),
            LocalTime.of(16, 0),
            null
        );
        
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule sch = invocation.getArgument(0);
            return new Schedule(
                new ScheduleId(2L),
                sch.getUserId(),
                sch.getDay(),
                sch.getStartTime(),
                sch.getEndTime(),
                sch.getType(),
                sch.isActive(),
                sch.getCreatedAt(),
                sch.getUpdatedAt()
            );
        });
        
        // When
        Schedule result = createScheduleService.create(command);
        
        // Then
        assertNotNull(result);
        assertEquals("Regular", result.getType()); // Default type
        
        verify(scheduleRepository).save(any(Schedule.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            null,
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createScheduleService.create(command)
        );
        
        verifyNoInteractions(scheduleRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenDayIsNull() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            1L,
            null,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createScheduleService.create(command)
        );
        
        verifyNoInteractions(scheduleRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            1L,
            "Monday",
            LocalTime.of(17, 0),
            LocalTime.of(9, 0), // End before start
            "Regular"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createScheduleService.create(command)
        );
        
        verifyNoInteractions(scheduleRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenEndTimeEqualsStartTime() {
        // Given
        CreateScheduleCommand command = new CreateScheduleCommand(
            1L,
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(9, 0), // Same time
            "Regular"
        );
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> createScheduleService.create(command)
        );
        
        verifyNoInteractions(scheduleRepository);
    }
}
