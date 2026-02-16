package com.sigrap.employee.application.service;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetScheduleService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class GetScheduleServiceTest {
    
    @Mock
    private ScheduleRepositoryPort scheduleRepository;
    
    @InjectMocks
    private GetScheduleService getScheduleService;
    
    @BeforeEach
    void setUp() {
        reset(scheduleRepository);
    }
    
    @Test
    void shouldGetScheduleById() {
        // Given
        ScheduleId id = new ScheduleId(1L);
        Schedule schedule = createSampleSchedule(id);
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        
        // When
        Schedule result = getScheduleService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(scheduleRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given
        ScheduleId id = new ScheduleId(999L);
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getScheduleService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(scheduleRepository).findById(id);
    }
    
    @Test
    void shouldFindScheduleById() {
        // Given
        ScheduleId id = new ScheduleId(1L);
        Schedule schedule = createSampleSchedule(id);
        
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        
        // When
        Optional<Schedule> result = getScheduleService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(scheduleRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenScheduleNotFound() {
        // Given
        ScheduleId id = new ScheduleId(999L);
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Schedule> result = getScheduleService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(scheduleRepository).findById(id);
    }
    
    @Test
    void shouldGetAllSchedules() {
        // Given
        List<Schedule> schedules = List.of(
            createSampleSchedule(new ScheduleId(1L)),
            createSampleSchedule(new ScheduleId(2L))
        );
        
        when(scheduleRepository.findAll()).thenReturn(schedules);
        
        // When
        List<Schedule> result = getScheduleService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(scheduleRepository).findAll();
    }
    
    @Test
    void shouldGetSchedulesByUserId() {
        // Given
        UserId userId = new UserId(1L);
        List<Schedule> schedules = List.of(
            createSampleSchedule(new ScheduleId(1L)),
            createSampleSchedule(new ScheduleId(2L))
        );
        
        when(scheduleRepository.findByUserId(userId)).thenReturn(schedules);
        
        // When
        List<Schedule> result = getScheduleService.getByUserId(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(scheduleRepository).findByUserId(userId);
    }
    
    @Test
    void shouldGetSchedulesByDay() {
        // Given
        String day = "Monday";
        List<Schedule> schedules = List.of(
            createSampleSchedule(new ScheduleId(1L))
        );
        
        when(scheduleRepository.findByDay(day)).thenReturn(schedules);
        
        // When
        List<Schedule> result = getScheduleService.getByDay(day);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository).findByDay(day);
    }
    
    private Schedule createSampleSchedule(ScheduleId id) {
        return new Schedule(
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
    }
}
