package com.sigrap.employee.application.port.in;

import com.sigrap.employee.application.port.in.command.UpdateScheduleCommand;
import com.sigrap.employee.domain.model.Schedule;
import com.sigrap.employee.domain.model.ScheduleId;

/**
 * Input port for updating an existing schedule.
 * This interface defines the use case for schedule updates.
 */
public interface UpdateScheduleUseCase {
    
    /**
     * Updates an existing schedule with the provided command data.
     *
     * @param id the schedule identifier
     * @param command the command containing schedule update data
     * @return the updated schedule domain entity
     * @throws IllegalArgumentException if the schedule is not found
     * @throws IllegalArgumentException if the update data is invalid
     */
    Schedule update(ScheduleId id, UpdateScheduleCommand command);
}
