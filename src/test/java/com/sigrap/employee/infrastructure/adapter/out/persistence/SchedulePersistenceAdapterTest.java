package com.sigrap.employee.infrastructure.adapter.out.persistence;

import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;
import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SchedulePersistenceAdapter.
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
class SchedulePersistenceAdapterTest {

    @Autowired
    private SchedulePersistenceAdapter adapter;

    @Autowired
    private ScheduleJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewSchedule() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        // When
        Schedule saved = adapter.save(schedule);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId().value());
        assertEquals("Monday", saved.getDay());
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(17, 0), saved.getEndTime());
        assertEquals("Regular", saved.getType());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingSchedule() {
        // Given - save initial schedule
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Tuesday",
            LocalTime.of(8, 0),
            LocalTime.of(16, 0),
            "Regular"
        );
        Schedule saved = adapter.save(schedule);
        
        // When - update the schedule
        Schedule updated = new Schedule(
            saved.getId(),
            saved.getUserId(),
            "Tuesday",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            "Overtime",
            true,
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Schedule result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(18, 0), result.getEndTime());
        assertEquals("Overtime", result.getType());
    }

    @Test
    void shouldFindScheduleById() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(2L),
            "Wednesday",
            LocalTime.of(10, 0),
            LocalTime.of(18, 0),
            "Part-time"
        );
        Schedule saved = adapter.save(schedule);

        // When
        Optional<Schedule> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(2L, found.get().getUserId().value());
        assertEquals("Wednesday", found.get().getDay());
    }

    @Test
    void shouldReturnEmptyWhenScheduleNotFound() {
        // Given
        ScheduleId nonExistentId = new ScheduleId(999L);

        // When
        Optional<Schedule> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllSchedules() {
        // Given
        adapter.save(new Schedule(new UserId(1L), "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(new UserId(2L), "Tuesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(new UserId(3L), "Wednesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));

        // When
        List<Schedule> schedules = adapter.findAll();

        // Then
        assertNotNull(schedules);
        assertEquals(3, schedules.size());
    }

    @Test
    void shouldFindSchedulesByUserId() {
        // Given
        UserId userId = new UserId(5L);
        adapter.save(new Schedule(userId, "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(userId, "Tuesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(new UserId(6L), "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));

        // When
        List<Schedule> schedules = adapter.findByUserId(userId);

        // Then
        assertNotNull(schedules);
        assertEquals(2, schedules.size());
        assertTrue(schedules.stream().allMatch(s -> s.getUserId().equals(userId)));
    }

    @Test
    void shouldFindSchedulesByDay() {
        // Given
        adapter.save(new Schedule(new UserId(1L), "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(new UserId(2L), "Monday", LocalTime.of(10, 0), LocalTime.of(18, 0), "Regular"));
        adapter.save(new Schedule(new UserId(3L), "Tuesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));

        // When
        List<Schedule> schedules = adapter.findByDay("Monday");

        // Then
        assertNotNull(schedules);
        assertEquals(2, schedules.size());
        assertTrue(schedules.stream().allMatch(s -> s.getDay().equals("Monday")));
    }

    @Test
    void shouldDeleteScheduleById() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Thursday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        Schedule saved = adapter.save(schedule);
        ScheduleId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Schedule> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldCountSchedules() {
        // Given
        adapter.save(new Schedule(new UserId(1L), "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));
        adapter.save(new Schedule(new UserId(2L), "Tuesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"));

        // When
        long count = adapter.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void shouldSaveAllSchedules() {
        // Given
        List<Schedule> schedules = List.of(
            new Schedule(new UserId(1L), "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"),
            new Schedule(new UserId(2L), "Tuesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular"),
            new Schedule(new UserId(3L), "Wednesday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular")
        );

        // When
        List<Schedule> saved = adapter.saveAll(schedules);

        // Then
        assertNotNull(saved);
        assertEquals(3, saved.size());
        assertTrue(saved.stream().allMatch(s -> s.getId() != null));
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Friday",
            LocalTime.of(8, 30),
            LocalTime.of(16, 30),
            "Flexible"
        );

        // When
        Schedule saved = adapter.save(schedule);
        Optional<Schedule> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Schedule result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(1L, result.getUserId().value());
        assertEquals("Friday", result.getDay());
        assertEquals(LocalTime.of(8, 30), result.getStartTime());
        assertEquals(LocalTime.of(16, 30), result.getEndTime());
        assertEquals("Flexible", result.getType());
        assertTrue(result.isActive());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldHandleScheduleWithNullType() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Saturday",
            LocalTime.of(9, 0),
            LocalTime.of(13, 0),
            null
        );

        // When
        Schedule saved = adapter.save(schedule);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Regular", saved.getType()); // Default value
    }

    @Test
    void shouldHandleInactiveSchedule() {
        // Given
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Sunday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        Schedule saved = adapter.save(schedule);
        
        // When - deactivate the schedule
        Schedule inactive = new Schedule(
            saved.getId(),
            saved.getUserId(),
            saved.getDay(),
            saved.getStartTime(),
            saved.getEndTime(),
            saved.getType(),
            false,
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Schedule result = adapter.save(inactive);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
    }
}
