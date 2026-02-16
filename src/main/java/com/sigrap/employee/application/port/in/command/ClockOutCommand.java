package com.sigrap.employee.application.port.in.command;

import java.time.LocalDateTime;

/**
 * Command for clocking out an employee.
 * This is an immutable data carrier that represents the user's intent to clock out.
 *
 * @param attendanceId the attendance record identifier
 * @param clockOutTime the clock-out time
 */
public record ClockOutCommand(
    Long attendanceId,
    LocalDateTime clockOutTime
) {}
