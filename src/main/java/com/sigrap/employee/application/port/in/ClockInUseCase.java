package com.sigrap.employee.application.port.in;

import com.sigrap.employee.application.port.in.command.ClockInCommand;
import com.sigrap.employee.domain.model.Attendance;

/**
 * Input port for clocking in an employee.
 * This interface defines the use case for recording employee clock-in time.
 */
public interface ClockInUseCase {
    
    /**
     * Records the clock-in time for an employee.
     *
     * @param command the command containing clock-in data
     * @return the updated attendance domain entity
     * @throws IllegalArgumentException if the user is not found
     * @throws IllegalStateException if the employee is already clocked in
     */
    Attendance clockIn(ClockInCommand command);
}
