package com.sigrap.employee.application.port.in;

import com.sigrap.employee.application.port.in.command.CreateScheduleCommand;
import com.sigrap.employee.domain.model.Schedule;

/**
 * Input port for creating a new schedule.
 * This interface defines the use case for schedule creation.
 */
public interface CreateScheduleUseCase {
    
    /**
     * Creates a new schedule with the provided command data.
     *
     * @param command the command containing schedule creation data
     * @return the created schedule domain entity
     * @throws IllegalArgumentException if the user is not found
     * @throws IllegalArgumentException if the schedule data is invalid
     */
    Schedule create(CreateScheduleCommand command);
}
