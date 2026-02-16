package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Attendance;
import com.sigrap.employee.domain.model.AttendanceId;
import com.sigrap.employee.domain.model.AttendanceStatus;
import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AttendancePersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttendancePersistenceAdapterTest {

    @Autowired
    private AttendancePersistenceAdapter adapter;

    @Autowired
    private AttendanceJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewAttendance() {
        // Given
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.of(2024, 1, 15, 8, 0),
            AttendanceStatus.PRESENT
        );

        // When
        Attendance saved = adapter.save(attendance);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId().value());
        assertEquals(AttendanceStatus.PRESENT, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingAttendance() {
        // Given - save initial attendance
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.of(2024, 1, 15, 8, 0),
            AttendanceStatus.PRESENT
        );
        Attendance saved = adapter.save(attendance);
        
        // When - update the attendance with clock-in time
        Attendance updated = new Attendance(
            saved.getId(),
            saved.getUserId(),
            saved.getDate(),
            LocalDateTime.of(2024, 1, 15, 8, 30),
            null,
            null,
            AttendanceStatus.LATE,
            saved.getCreatedAt(),
            LocalDateTime.now()
        );
        Attendance result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(AttendanceStatus.LATE, result.getStatus());
        assertNotNull(result.getClockInTime());
    }

    @Test
    void shouldFindAttendanceById() {
        // Given
        Attendance attendance = new Attendance(
            new UserId(2L),
            LocalDateTime.of(2024, 1, 16, 9, 0),
            AttendanceStatus.PRESENT
        );
        Attendance saved = adapter.save(attendance);

        // When
        Optional<Attendance> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(2L, found.get().getUserId().value());
    }

    @Test
    void shouldReturnEmptyWhenAttendanceNotFound() {
        // Given
        AttendanceId nonExistentId = new AttendanceId(999L);

        // When
        Optional<Attendance> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllAttendances() {
        // Given
        adapter.save(new Attendance(new UserId(1L), LocalDateTime.now(), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(new UserId(2L), LocalDateTime.now(), AttendanceStatus.ABSENT));
        adapter.save(new Attendance(new UserId(3L), LocalDateTime.now(), AttendanceStatus.LATE));

        // When
        List<Attendance> attendances = adapter.findAll();

        // Then
        assertNotNull(attendances);
        assertEquals(3, attendances.size());
    }

    @Test
    void shouldFindAttendancesByUserId() {
        // Given
        UserId userId = new UserId(5L);
        adapter.save(new Attendance(userId, LocalDateTime.of(2024, 1, 1, 8, 0), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(userId, LocalDateTime.of(2024, 1, 2, 8, 0), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(new UserId(6L), LocalDateTime.of(2024, 1, 1, 8, 0), AttendanceStatus.PRESENT));

        // When
        List<Attendance> attendances = adapter.findByUserId(userId);

        // Then
        assertNotNull(attendances);
        assertEquals(2, attendances.size());
        assertTrue(attendances.stream().allMatch(a -> a.getUserId().equals(userId)));
    }

    @Test
    void shouldFindAttendancesByDateRange() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 20, 23, 59);
        
        adapter.save(new Attendance(new UserId(1L), LocalDateTime.of(2024, 1, 5, 8, 0), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(new UserId(1L), LocalDateTime.of(2024, 1, 15, 8, 0), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(new UserId(1L), LocalDateTime.of(2024, 1, 25, 8, 0), AttendanceStatus.PRESENT));

        // When
        List<Attendance> attendances = adapter.findByDateRange(start, end);

        // Then
        assertNotNull(attendances);
        assertEquals(1, attendances.size());
        assertTrue(attendances.get(0).getDate().isAfter(start.minusSeconds(1)));
        assertTrue(attendances.get(0).getDate().isBefore(end.plusSeconds(1)));
    }

    @Test
    void shouldDeleteAttendanceById() {
        // Given
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        Attendance saved = adapter.save(attendance);
        AttendanceId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Attendance> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldCountAttendances() {
        // Given
        adapter.save(new Attendance(new UserId(1L), LocalDateTime.now(), AttendanceStatus.PRESENT));
        adapter.save(new Attendance(new UserId(2L), LocalDateTime.now(), AttendanceStatus.ABSENT));

        // When
        long count = adapter.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void shouldSaveAllAttendances() {
        // Given
        List<Attendance> attendances = List.of(
            new Attendance(new UserId(1L), LocalDateTime.now(), AttendanceStatus.PRESENT),
            new Attendance(new UserId(2L), LocalDateTime.now(), AttendanceStatus.ABSENT),
            new Attendance(new UserId(3L), LocalDateTime.now(), AttendanceStatus.LATE)
        );

        // When
        List<Attendance> saved = adapter.saveAll(attendances);

        // Then
        assertNotNull(saved);
        assertEquals(3, saved.size());
        assertTrue(saved.stream().allMatch(a -> a.getId() != null));
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 8, 0);
        LocalDateTime clockIn = LocalDateTime.of(2024, 1, 15, 8, 5);
        LocalDateTime clockOut = LocalDateTime.of(2024, 1, 15, 17, 0);
        
        Attendance attendance = new Attendance(
            null,
            new UserId(1L),
            date,
            clockIn,
            clockOut,
            8.92,
            AttendanceStatus.PRESENT,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When
        Attendance saved = adapter.save(attendance);
        Optional<Attendance> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Attendance result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(1L, result.getUserId().value());
        assertEquals(date, result.getDate());
        assertEquals(clockIn, result.getClockInTime());
        assertEquals(clockOut, result.getClockOutTime());
        assertEquals(8.92, result.getTotalHours());
        assertEquals(AttendanceStatus.PRESENT, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldHandleAttendanceWithNullOptionalFields() {
        // Given
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.ABSENT
        );

        // When
        Attendance saved = adapter.save(attendance);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNull(saved.getClockInTime());
        assertNull(saved.getClockOutTime());
        assertNull(saved.getTotalHours());
    }
}
