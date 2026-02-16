package com.sigrap.employee.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.employee.application.port.in.command.ClockInCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClockInService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class ClockInServiceTest {
    
    @Mock
    private AttendanceRepositoryPort attendanceRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private ClockInService clockInService;
    
    @BeforeEach
    void setUp() {
        reset(attendanceRepository, eventPublisher);
    }
    
    @Test
    void shouldClockInSuccessfully() {
        // Given
        LocalDateTime clockInTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        ClockInCommand command = new ClockInCommand(1L, clockInTime);
        
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance att = invocation.getArgument(0);
            return new Attendance(
                new AttendanceId(1L),
                att.getUserId(),
                att.getDate(),
                att.getClockInTime(),
                att.getClockOutTime(),
                att.getTotalHours(),
                att.getStatus(),
                att.getCreatedAt(),
                att.getUpdatedAt()
            );
        });
        
        // When
        Attendance result = clockInService.clockIn(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(new UserId(1L), result.getUserId());
        assertEquals(clockInTime, result.getClockInTime());
        assertNull(result.getClockOutTime());
        assertEquals(AttendanceStatus.PRESENT, result.getStatus());
        assertTrue(result.isClockedIn());
        
        verify(attendanceRepository).save(any(Attendance.class));
        verify(eventPublisher).publish(any());
    }
    
    @Test
    void shouldClockInWithCurrentTimeWhenNotProvided() {
        // Given
        ClockInCommand command = new ClockInCommand(1L, null);
        
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance att = invocation.getArgument(0);
            return new Attendance(
                new AttendanceId(1L),
                att.getUserId(),
                att.getDate(),
                att.getClockInTime(),
                att.getClockOutTime(),
                att.getTotalHours(),
                att.getStatus(),
                att.getCreatedAt(),
                att.getUpdatedAt()
            );
        });
        
        // When
        Attendance result = clockInService.clockIn(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getClockInTime());
        assertTrue(result.isClockedIn());
        
        verify(attendanceRepository).save(any(Attendance.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // Given
        ClockInCommand command = new ClockInCommand(null, LocalDateTime.now());
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> clockInService.clockIn(command)
        );
        
        verifyNoInteractions(attendanceRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid() {
        // Given
        ClockInCommand command = new ClockInCommand(0L, LocalDateTime.now());
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> clockInService.clockIn(command)
        );
        
        verifyNoInteractions(attendanceRepository);
    }
}
