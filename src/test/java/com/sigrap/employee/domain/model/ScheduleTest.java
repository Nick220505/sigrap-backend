package com.sigrap.employee.domain.model;

import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Schedule domain entity.
 * Tests business logic without any framework dependencies.
 */
class ScheduleTest {

    @Test
    void shouldCreateScheduleWithValidData() {
        UserId userId = new UserId(1L);
        String day = "Monday";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        String type = "Regular";

        Schedule schedule = new Schedule(userId, day, startTime, endTime, type);

        assertNull(schedule.getId());
        assertEquals(userId, schedule.getUserId());
        assertEquals(day, schedule.getDay());
        assertEquals(startTime, schedule.getStartTime());
        assertEquals(endTime, schedule.getEndTime());
        assertEquals(type, schedule.getType());
        assertTrue(schedule.isActive());
        assertTrue(schedule.isNew());
    }

    @Test
    void shouldDefaultToRegularTypeWhenNull() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            null
        );

        assertEquals("Regular", schedule.getType());
    }

    @Test
    void shouldThrowExceptionForNullUserId() {
        assertThrows(NullPointerException.class, () ->
            new Schedule(null, "Monday", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular")
        );
    }

    @Test
    void shouldThrowExceptionForNullDay() {
        assertThrows(IllegalArgumentException.class, () ->
            new Schedule(new UserId(1L), null, LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular")
        );
    }

    @Test
    void shouldThrowExceptionForBlankDay() {
        assertThrows(IllegalArgumentException.class, () ->
            new Schedule(new UserId(1L), "  ", LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular")
        );
    }

    @Test
    void shouldThrowExceptionForNullStartTime() {
        assertThrows(NullPointerException.class, () ->
            new Schedule(new UserId(1L), "Monday", null, LocalTime.of(17, 0), "Regular")
        );
    }

    @Test
    void shouldThrowExceptionForNullEndTime() {
        assertThrows(NullPointerException.class, () ->
            new Schedule(new UserId(1L), "Monday", LocalTime.of(9, 0), null, "Regular")
        );
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        assertThrows(IllegalArgumentException.class, () ->
            new Schedule(
                new UserId(1L),
                "Monday",
                LocalTime.of(17, 0),
                LocalTime.of(9, 0),
                "Regular"
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenEndTimeEqualsStartTime() {
        LocalTime time = LocalTime.of(9, 0);
        assertThrows(IllegalArgumentException.class, () ->
            new Schedule(new UserId(1L), "Monday", time, time, "Regular")
        );
    }

    @Test
    void shouldUpdateDay() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        schedule.updateDay("Tuesday");

        assertEquals("Tuesday", schedule.getDay());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToNullDay() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        assertThrows(IllegalArgumentException.class, () ->
            schedule.updateDay(null)
        );
    }

    @Test
    void shouldUpdateTimes() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );
        LocalTime newStartTime = LocalTime.of(8, 0);
        LocalTime newEndTime = LocalTime.of(16, 0);

        schedule.updateTimes(newStartTime, newEndTime);

        assertEquals(newStartTime, schedule.getStartTime());
        assertEquals(newEndTime, schedule.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidTimes() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        assertThrows(IllegalArgumentException.class, () ->
            schedule.updateTimes(LocalTime.of(17, 0), LocalTime.of(9, 0))
        );
    }

    @Test
    void shouldUpdateType() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        schedule.updateType("Overtime");

        assertEquals("Overtime", schedule.getType());
    }

    @Test
    void shouldActivateSchedule() {
        Schedule schedule = new Schedule(
            new ScheduleId(1L),
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular",
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        schedule.activate();

        assertTrue(schedule.isActive());
    }

    @Test
    void shouldDeactivateSchedule() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            "Regular"
        );

        schedule.deactivate();

        assertFalse(schedule.isActive());
    }

    @Test
    void shouldCalculateDurationInHours() {
        Schedule schedule = new Schedule(
            new UserId(1L),
            "Monday",
            LocalTime.of(9, 0),
            LocalTime.of(17, 30),
            "Regular"
        );

        double duration = schedule.getDurationInHours();

        assertEquals(8.5, duration, 0.01);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        ScheduleId id = new ScheduleId(1L);
        Schedule schedule1 = new Schedule(
            id, new UserId(1L), "Monday",
            LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular",
            true, LocalDateTime.now(), LocalDateTime.now()
        );
        Schedule schedule2 = new Schedule(
            id, new UserId(2L), "Tuesday",
            LocalTime.of(8, 0), LocalTime.of(16, 0), "Overtime",
            false, LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals(schedule1, schedule2);
        assertEquals(schedule1.hashCode(), schedule2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Schedule schedule1 = new Schedule(
            new ScheduleId(1L), new UserId(1L), "Monday",
            LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular",
            true, LocalDateTime.now(), LocalDateTime.now()
        );
        Schedule schedule2 = new Schedule(
            new ScheduleId(2L), new UserId(1L), "Monday",
            LocalTime.of(9, 0), LocalTime.of(17, 0), "Regular",
            true, LocalDateTime.now(), LocalDateTime.now()
        );

        assertNotEquals(schedule1, schedule2);
    }
}
