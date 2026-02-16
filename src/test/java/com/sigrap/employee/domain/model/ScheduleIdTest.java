package com.sigrap.employee.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScheduleId value object.
 */
class ScheduleIdTest {

    @Test
    void shouldCreateScheduleIdWithValidValue() {
        ScheduleId id = new ScheduleId(1L);
        
        assertEquals(1L, id.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new ScheduleId(null)
        );
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new ScheduleId(0L)
        );
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        assertThrows(IllegalArgumentException.class, () ->
            new ScheduleId(-1L)
        );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        ScheduleId id1 = new ScheduleId(1L);
        ScheduleId id2 = new ScheduleId(1L);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        ScheduleId id1 = new ScheduleId(1L);
        ScheduleId id2 = new ScheduleId(2L);
        
        assertNotEquals(id1, id2);
    }
}
