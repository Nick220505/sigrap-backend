package com.sigrap.employee.application.service;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetAttendanceService.
 * Tests the use case implementation with mocked repository port.
 */
@ExtendWith(MockitoExtension.class)
class GetAttendanceServiceTest {
    
    @Mock
    private AttendanceRepositoryPort attendanceRepository;
    
    @InjectMocks
    private GetAttendanceService getAttendanceService;
    
    @BeforeEach
    void setUp() {
        reset(attendanceRepository);
    }
    
    @Test
    void shouldGetAttendanceById() {
        // Given
        AttendanceId id = new AttendanceId(1L);
        Attendance attendance = createSampleAttendance(id);
        
        when(attendanceRepository.findById(id)).thenReturn(Optional.of(attendance));
        
        // When
        Attendance result = getAttendanceService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(attendanceRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenAttendanceNotFound() {
        // Given
        AttendanceId id = new AttendanceId(999L);
        when(attendanceRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getAttendanceService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(attendanceRepository).findById(id);
    }
    
    @Test
    void shouldFindAttendanceById() {
        // Given
        AttendanceId id = new AttendanceId(1L);
        Attendance attendance = createSampleAttendance(id);
        
        when(attendanceRepository.findById(id)).thenReturn(Optional.of(attendance));
        
        // When
        Optional<Attendance> result = getAttendanceService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(attendanceRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenAttendanceNotFound() {
        // Given
        AttendanceId id = new AttendanceId(999L);
        when(attendanceRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Attendance> result = getAttendanceService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(attendanceRepository).findById(id);
    }
    
    @Test
    void shouldGetAllAttendances() {
        // Given
        List<Attendance> attendances = List.of(
            createSampleAttendance(new AttendanceId(1L)),
            createSampleAttendance(new AttendanceId(2L))
        );
        
        when(attendanceRepository.findAll()).thenReturn(attendances);
        
        // When
        List<Attendance> result = getAttendanceService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(attendanceRepository).findAll();
    }
    
    @Test
    void shouldGetAttendancesByUserId() {
        // Given
        UserId userId = new UserId(1L);
        List<Attendance> attendances = List.of(
            createSampleAttendance(new AttendanceId(1L)),
            createSampleAttendance(new AttendanceId(2L))
        );
        
        when(attendanceRepository.findByUserId(userId)).thenReturn(attendances);
        
        // When
        List<Attendance> result = getAttendanceService.getByUserId(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(attendanceRepository).findByUserId(userId);
    }
    
    @Test
    void shouldGetAttendancesByDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        List<Attendance> attendances = List.of(
            createSampleAttendance(new AttendanceId(1L))
        );
        
        when(attendanceRepository.findByDateRange(startDate, endDate)).thenReturn(attendances);
        
        // When
        List<Attendance> result = getAttendanceService.getByDateRange(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(attendanceRepository).findByDateRange(startDate, endDate);
    }
    
    private Attendance createSampleAttendance(AttendanceId id) {
        return new Attendance(
            id,
            new UserId(1L),
            LocalDateTime.of(2024, 1, 15, 0, 0),
            LocalDateTime.of(2024, 1, 15, 9, 0),
            LocalDateTime.of(2024, 1, 15, 17, 0),
            8.0,
            AttendanceStatus.PRESENT,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
