package com.sigrap.employee.application.port.in;

import com.sigrap.employee.application.port.in.command.ClockOutCommand;
import com.sigrap.employee.domain.model.Attendance;

/**
 * Input port for clocking out an employee.
 * This interface defines the use case for recording employee clock-out time.
 */
public interface ClockOutUseCase {
    
    /**
     * Records the clock-out time for an employee.
     *
     * @param command the command containing clock-out data
     * @return the updated attendance domain entity
     * @throws IllegalArgumentException if the attendance record is not found
     * @throws IllegalStateException if the employee is not clocked in or already clocked out
     */
    Attendance clockOut(ClockOutCommand command);
}
