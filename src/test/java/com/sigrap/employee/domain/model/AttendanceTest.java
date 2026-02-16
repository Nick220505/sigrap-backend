package com.sigrap.employee.domain.model;

import com.sigrap.user.domain.model.UserId;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Attendance domain entity.
 * Tests business logic without any framework dependencies.
 */
class AttendanceTest {

    @Test
    void shouldCreateAttendanceWithValidData() {
        UserId userId = new UserId(1L);
        LocalDateTime date = LocalDateTime.now();
        AttendanceStatus status = AttendanceStatus.PRESENT;

        Attendance attendance = new Attendance(userId, date, status);

        assertNull(attendance.getId());
        assertEquals(userId, attendance.getUserId());
        assertEquals(date, attendance.getDate());
        assertEquals(status, attendance.getStatus());
        assertNull(attendance.getClockInTime());
        assertNull(attendance.getClockOutTime());
        assertNull(attendance.getTotalHours());
        assertTrue(attendance.isNew());
    }

    @Test
    void shouldThrowExceptionForNullUserId() {
        LocalDateTime date = LocalDateTime.now();
        
        assertThrows(NullPointerException.class, () ->
            new Attendance(null, date, AttendanceStatus.PRESENT)
        );
    }

    @Test
    void shouldThrowExceptionForNullDate() {
        UserId userId = new UserId(1L);
        
        assertThrows(NullPointerException.class, () ->
            new Attendance(userId, null, AttendanceStatus.PRESENT)
        );
    }

    @Test
    void shouldThrowExceptionForNullStatus() {
        UserId userId = new UserId(1L);
        LocalDateTime date = LocalDateTime.now();
        
        assertThrows(NullPointerException.class, () ->
            new Attendance(userId, date, null)
        );
    }

    @Test
    void shouldClockInSuccessfully() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        LocalDateTime clockInTime = LocalDateTime.now();

        attendance.clockIn(clockInTime);

        assertEquals(clockInTime, attendance.getClockInTime());
        assertTrue(attendance.isClockedIn());
        assertFalse(attendance.isComplete());
    }

    @Test
    void shouldThrowExceptionWhenClockingInTwice() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        attendance.clockIn(LocalDateTime.now());

        assertThrows(IllegalStateException.class, () ->
            attendance.clockIn(LocalDateTime.now())
        );
    }

    @Test
    void shouldClockOutSuccessfully() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        LocalDateTime clockInTime = LocalDateTime.now();
        LocalDateTime clockOutTime = clockInTime.plusHours(8);

        attendance.clockIn(clockInTime);
        attendance.clockOut(clockOutTime);

        assertEquals(clockOutTime, attendance.getClockOutTime());
        assertFalse(attendance.isClockedIn());
        assertTrue(attendance.isComplete());
        assertNotNull(attendance.getTotalHours());
        assertEquals(8.0, attendance.getTotalHours(), 0.01);
    }

    @Test
    void shouldThrowExceptionWhenClockingOutWithoutClockingIn() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );

        assertThrows(IllegalStateException.class, () ->
            attendance.clockOut(LocalDateTime.now())
        );
    }

    @Test
    void shouldThrowExceptionWhenClockingOutTwice() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        LocalDateTime clockInTime = LocalDateTime.now();
        attendance.clockIn(clockInTime);
        attendance.clockOut(clockInTime.plusHours(8));

        assertThrows(IllegalStateException.class, () ->
            attendance.clockOut(LocalDateTime.now())
        );
    }

    @Test
    void shouldThrowExceptionWhenClockOutTimeIsBeforeClockInTime() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        LocalDateTime clockInTime = LocalDateTime.now();
        attendance.clockIn(clockInTime);

        assertThrows(IllegalArgumentException.class, () ->
            attendance.clockOut(clockInTime.minusHours(1))
        );
    }

    @Test
    void shouldCalculateTotalHoursCorrectly() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );
        LocalDateTime clockInTime = LocalDateTime.of(2024, 1, 1, 9, 0);
        LocalDateTime clockOutTime = LocalDateTime.of(2024, 1, 1, 17, 30);

        attendance.clockIn(clockInTime);
        attendance.clockOut(clockOutTime);

        assertEquals(8.5, attendance.getTotalHours(), 0.01);
    }

    @Test
    void shouldUpdateStatus() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.PRESENT
        );

        attendance.updateStatus(AttendanceStatus.LATE);

        assertEquals(AttendanceStatus.LATE, attendance.getStatus());
    }

    @Test
    void shouldChangeStatusToPresentWhenClockingInFromAbsent() {
        Attendance attendance = new Attendance(
            new UserId(1L),
            LocalDateTime.now(),
            AttendanceStatus.ABSENT
        );

        attendance.clockIn(LocalDateTime.now());

        assertEquals(AttendanceStatus.PRESENT, attendance.getStatus());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        AttendanceId id = new AttendanceId(1L);
        Attendance attendance1 = new Attendance(
            id, new UserId(1L), LocalDateTime.now(),
            null, null, null, AttendanceStatus.PRESENT,
            LocalDateTime.now(), LocalDateTime.now()
        );
        Attendance attendance2 = new Attendance(
            id, new UserId(2L), LocalDateTime.now(),
            null, null, null, AttendanceStatus.ABSENT,
            LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals(attendance1, attendance2);
        assertEquals(attendance1.hashCode(), attendance2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Attendance attendance1 = new Attendance(
            new AttendanceId(1L), new UserId(1L), LocalDateTime.now(),
            null, null, null, AttendanceStatus.PRESENT,
            LocalDateTime.now(), LocalDateTime.now()
        );
        Attendance attendance2 = new Attendance(
            new AttendanceId(2L), new UserId(1L), LocalDateTime.now(),
            null, null, null, AttendanceStatus.PRESENT,
            LocalDateTime.now(), LocalDateTime.now()
        );

        assertNotEquals(attendance1, attendance2);
    }
}
