package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.employee.application.port.in.command.ClockOutCommand;
import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.model.AttendanceStatus;
import com.sigrap.employee.domain.port.AttendanceRepositoryPort;
import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClockOutService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class ClockOutServiceTest {
    
    @Mock
    private AttendanceRepositoryPort attendanceRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private ClockOutService clockOutService;
    
    @BeforeEach
    void setUp() {
        reset(attendanceRepository, eventPublisher);
    }
    
    @Test
    void shouldClockOutSuccessfully() {
        // Given
        LocalDateTime clockInTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime clockOutTime = LocalDateTime.of(2024, 1, 15, 17, 0);
        
        Attendance existingAttendance = new Attendance(
            new AttendanceId(1L),
            new UserId(1L),
            clockInTime.toLocalDate().atStartOfDay(),
            clockInTime,
            null,
            null,
            AttendanceStatus.PRESENT,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        ClockOutCommand command = new ClockOutCommand(1L, clockOutTime);
        
        when(attendanceRepository.findById(any(AttendanceId.class)))
            .thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Attendance result = clockOutService.clockOut(command);
        
        // Then
        assertNotNull(result);
        assertEquals(clockInTime, result.getClockInTime());
        assertEquals(clockOutTime, result.getClockOutTime());
        assertNotNull(result.getTotalHours());
        assertEquals(8.0, result.getTotalHours(), 0.01);
        assertTrue(result.isComplete());
        assertFalse(result.isClockedIn());
        
        verify(attendanceRepository).findById(any(AttendanceId.class));
        verify(attendanceRepository).save(any(Attendance.class));
        verify(eventPublisher).publish(any());
    }
    
    @Test
    void shouldThrowExceptionWhenAttendanceNotFound() {
        // Given
        ClockOutCommand command = new ClockOutCommand(999L, LocalDateTime.now());
        
        when(attendanceRepository.findById(any(AttendanceId.class)))
            .thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clockOutService.clockOut(command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(attendanceRepository).findById(any(AttendanceId.class));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNotClockedIn() {
        // Given
        Attendance attendanceWithoutClockIn = new Attendance(
            new UserId(1L),
            LocalDateTime.now().toLocalDate().atStartOfDay(),
            AttendanceStatus.ABSENT
        );
        
        Attendance persistedAttendance = new Attendance(
            new AttendanceId(1L),
            attendanceWithoutClockIn.getUserId(),
            attendanceWithoutClockIn.getDate(),
            null,
            null,
            null,
            attendanceWithoutClockIn.getStatus(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        ClockOutCommand command = new ClockOutCommand(1L, LocalDateTime.now());
        
        when(attendanceRepository.findById(any(AttendanceId.class)))
            .thenReturn(Optional.of(persistedAttendance));
        
        // When & Then
        assertThrows(
            IllegalStateException.class,
            () -> clockOutService.clockOut(command)
        );
        
        verify(attendanceRepository).findById(any(AttendanceId.class));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }
    
    @Test
    void shouldThrowExceptionWhenClockOutTimeBeforeClockIn() {
        // Given
        LocalDateTime clockInTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime clockOutTime = LocalDateTime.of(2024, 1, 15, 8, 0); // Before clock-in
        
        Attendance existingAttendance = new Attendance(
            new AttendanceId(1L),
            new UserId(1L),
            clockInTime.toLocalDate().atStartOfDay(),
            clockInTime,
            null,
            null,
            AttendanceStatus.PRESENT,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        ClockOutCommand command = new ClockOutCommand(1L, clockOutTime);
        
        when(attendanceRepository.findById(any(AttendanceId.class)))
            .thenReturn(Optional.of(existingAttendance));
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> clockOutService.clockOut(command)
        );
        
        verify(attendanceRepository).findById(any(AttendanceId.class));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }
}
