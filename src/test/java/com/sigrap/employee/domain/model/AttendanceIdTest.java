package com.sigrap.employee.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AttendanceId value object.
 */
class AttendanceIdTest {

    @Test
    void shouldCreateAttendanceIdWithValidValue() {
        AttendanceId id = new AttendanceId(1L);
        
        assertEquals(1L, id.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new AttendanceId(null)
        );
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new AttendanceId(0L)
        );
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new AttendanceId(-1L)
        );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        AttendanceId id1 = new AttendanceId(1L);
        AttendanceId id2 = new AttendanceId(1L);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        AttendanceId id1 = new AttendanceId(1L);
        AttendanceId id2 = new AttendanceId(2L);
        
        assertNotEquals(id1, id2);
    }
}
