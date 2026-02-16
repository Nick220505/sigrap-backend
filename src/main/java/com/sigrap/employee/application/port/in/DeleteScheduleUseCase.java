package com.sigrap.employee.application.port.in;

import com.sigrap.employee.domain.model.ScheduleId;

/**
 * Input port for deleting a schedule.
 * This interface defines the use case for schedule deletion.
 */
public interface DeleteScheduleUseCase {
    
    /**
     * Deletes a schedule by its identifier.
     *
     * @param id the schedule identifier
     * @throws IllegalArgumentException if the schedule is not found
     */
    void delete(ScheduleId id);
}
